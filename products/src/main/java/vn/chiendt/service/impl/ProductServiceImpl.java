package vn.chiendt.service.impl;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.chiendt.avro.ProductEvent;
import vn.chiendt.avro.ProductEventType;
import vn.chiendt.cache.redisson.annotation.DistributedLock;
import vn.chiendt.cache.redisson.service.RedissonCacheService;
import vn.chiendt.common.ProductStatus;
import vn.chiendt.dto.request.ProductCreationRequest;
import vn.chiendt.dto.request.ProductUpdateRequest;
import vn.chiendt.dto.response.ProductResponse;
import vn.chiendt.exception.InvalidDataException;
import vn.chiendt.mapper.ProductMapper;
import vn.chiendt.model.Product;
import vn.chiendt.model.ProductDocument;
import vn.chiendt.repository.ProductRepository;
import vn.chiendt.repository.ProductSearchRepository;
import vn.chiendt.service.ProductService;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.TimeUnit;

import static vn.chiendt.common.AvroConvert.toByteBuffer;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-SERVICE")
public class ProductServiceImpl implements ProductService {

    private final ProductSearchRepository productSearchRepository;
    private final ProductRepository productRepository;
    private final RedissonCacheService redissonCacheService;
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;
    private final ProductMapper productMapper;


    @Value("${kafka.topic}")
    private String productSyncEvents;

    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final String PRODUCT_SEARCH_CACHE_PREFIX = "product:search:";
    private static final long CACHE_TTL_MINUTES = 5;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(key = "'product:add:' + #request.name", waitTime = 5000, leaseTime = 10000)
    public ProductResponse addProduct(ProductCreationRequest request) throws JsonProcessingException {

        log.info("Add product {}", request);

        // Check if product with same name already exists
        String cacheKey = PRODUCT_CACHE_PREFIX + "name:" + request.getName();
        if (redissonCacheService.exists(cacheKey)) {
            log.warn("Product with name {} already exists", request.getName());
            throw new IllegalArgumentException("Product with this name already exists");
        }
        // save to RDMS
        Product product = new Product();
        product.setUserId(request.getUserId());
        product.setName(request.getName());
        product.setStatus(ProductStatus.ACTIVE);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        if (request.getAttributes() != null) {
            product.setAttributes(request.getAttributes());
        }

        Product productWithoutSlug = productRepository.save(product);
        log.info("Add product to DBMS {}", productWithoutSlug);

        // create slug for product
        Slugify slugify = new Slugify();
        String slug = slugify.slugify(productWithoutSlug.getName());
        productWithoutSlug.setSlug(Boolean.TRUE.equals(productRepository.existsBySlug(slug)) ?  productWithoutSlug.getSlug() : slug);

        Product result = productRepository.save(productWithoutSlug);

        //sync elastic search
        ProductEvent productEvent = ProductEvent.newBuilder()
                .setEventType(ProductEventType.CREATED)
                .setId(result.getId())
                .setSlug(result.getSlug())
                .setStatus(vn.chiendt.avro.ProductStatus.valueOf(result.getStatus().name()))
                .setName(result.getName())
                .setDescription(result.getDescription())
                .setPrice(toByteBuffer(result.getPrice(), 10, 2))
                .setUserId(result.getUserId())
                .setAttributes(objectMapper.writeValueAsString(result.getAttributes()) )
                .setCreatedAt(result.getCreatedAt().toEpochMilli())
                .setUpdatedAt(result.getUpdatedAt().toEpochMilli())
                .setTimestamp(System.currentTimeMillis())
                .build();
        kafkaTemplate.send(productSyncEvents, productEvent);
        log.info("ProductDocument saved with id {}", productEvent.getId());

        // Cache the product
        String productCacheKey = PRODUCT_CACHE_PREFIX + result.getId();
        redissonCacheService.put(productCacheKey, result, CACHE_TTL_MINUTES, TimeUnit.MINUTES);

        // Cache by name for duplicate check
        redissonCacheService.put(cacheKey, result.getId(), CACHE_TTL_MINUTES, TimeUnit.MINUTES);



        return productMapper.toProductResponse(result);
    }

    @Override
    @DistributedLock(key = "'product:update:' + #request.id", waitTime = 5000, leaseTime = 10000)
    public void updateProduct(ProductUpdateRequest request) throws JsonProcessingException {
        log.info("Update product {}", request);

        Product product = getProductByProductId(request.getId());
        product.setName(request.getName());
        product.setStatus(request.getStatus());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUserId(request.getUserId());

        if (request.getAttributes() != null) {
            product.setAttributes(request.getAttributes());
        }

        productRepository.save(product);

        //sync elastic search
        ProductEvent productUpdatedEvent = ProductEvent.newBuilder()
                .setEventType(ProductEventType.UPDATED)
                .setId(product.getId())
                .setStatus(vn.chiendt.avro.ProductStatus.valueOf(product.getStatus().name()))
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setPrice(toByteBuffer(product.getPrice(), 10, 2))
                .setUserId(product.getUserId())
                .setAttributes(objectMapper.writeValueAsString(product.getAttributes()) )
                .setCreatedAt(product.getCreatedAt().toEpochMilli())
                .setUpdatedAt(product.getUpdatedAt().toEpochMilli())
                .setTimestamp(System.currentTimeMillis())
                .build();
        kafkaTemplate.send(productSyncEvents, productUpdatedEvent);
        log.info("Product updated kafka with id {}", productUpdatedEvent.getId());



        // Update cache
        String productCacheKey = PRODUCT_CACHE_PREFIX + product.getId();
        redissonCacheService.put(productCacheKey, product, CACHE_TTL_MINUTES, TimeUnit.MINUTES);

        // Clear search cache as product name might have changed
        clearSearchCache();

    }

    @Override
    @DistributedLock(key = "'product:delete:' + #productId", waitTime = 5000, leaseTime = 10000)
    public void deleteProduct(long productId) {
        log.info("Delete product with id: {}", productId);
        
        // Get product info before deletion for cache cleanup
        Product product = getProductByProductId(productId);
        
        productRepository.deleteById(productId);
        productSearchRepository.deleteById(productId);
        
        // Clear cache
        String productCacheKey = PRODUCT_CACHE_PREFIX + productId;
        redissonCacheService.delete(productCacheKey);
        
        // Clear name cache if exists
        String nameCacheKey = PRODUCT_CACHE_PREFIX + "name:" + product.getName();
        redissonCacheService.delete(nameCacheKey);
        
        // Clear search cache
        clearSearchCache();
        
        log.info("Product {} deleted successfully", productId);
    }
    @Override
    public List<ProductDocument> searchProduct(String name) {
        log.info("Search products by name {}", name);

        // Check cache first
        String cacheKey = PRODUCT_SEARCH_CACHE_PREFIX + (StringUtils.hasLength(name) ? name : "all");
        List<ProductDocument> cachedResult = redissonCacheService.get(cacheKey);
        if (cachedResult != null) {
            log.debug("Returning cached search results for key: {}", cacheKey);
            return cachedResult;
        }

        List<ProductDocument> productDocuments = new ArrayList<>();

        if (StringUtils.hasLength(name)) {
            productDocuments = productSearchRepository.findByNameContaining(name);
        } else {
            Iterable<ProductDocument> documents = productSearchRepository.findAll();
            for (ProductDocument productDocument : documents) {
                productDocuments.add(productDocument);
            }
        }

        // Cache the results
        redissonCacheService.put(cacheKey, productDocuments, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("Cached search results for key: {}", cacheKey);

        return productDocuments;
    }

    @Override
    public ProductDocument getProductDocumentById(Long id) {
        log.info("Get product by id, id={}", id);
        
        // Check cache first
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        ProductDocument cachedProduct = redissonCacheService.get(cacheKey);
        if (cachedProduct != null) {
            log.debug("Returning cached product for id: {}", id);
            return cachedProduct;
        }
        
        // Get from database and cache
        ProductDocument product = getProductDocumentById(id);
        redissonCacheService.put(cacheKey, product, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("Cached product for id: {}", id);
        
        return product;
    }

    /**
     * Get product by id
     *
     * @param id
     * @return
     */
    private Product getProductByProductId(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    
    /**
     * Clear all search cache entries
     */
    private void clearSearchCache() {
        try {
            // Get all search cache keys
            var searchKeys = redissonCacheService.getKeys(PRODUCT_SEARCH_CACHE_PREFIX + "*");
            if (!searchKeys.isEmpty()) {
                redissonCacheService.delete(searchKeys);
                log.debug("Cleared {} search cache entries", searchKeys.size());
            }
        } catch (Exception e) {
            log.warn("Failed to clear search cache", e);
        }
    }


}
