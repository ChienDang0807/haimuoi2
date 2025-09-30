package vn.chiendt.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.chiendt.cache.redisson.annotation.DistributedLock;
import vn.chiendt.cache.redisson.service.RedissonCacheService;
import vn.chiendt.dto.request.ProductCreationRequest;
import vn.chiendt.dto.request.ProductUpdateRequest;
import vn.chiendt.model.Product;
import vn.chiendt.model.ProductDocument;
import vn.chiendt.repository.ProductRepository;
import vn.chiendt.repository.ProductSearchRepository;
import vn.chiendt.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-SERVICE")
public class ProductServiceImpl implements ProductService {

    private final ProductSearchRepository productSearchRepository;
    private final ProductRepository productRepository;
    private final RedissonCacheService redissonCacheService;
    
    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final String PRODUCT_SEARCH_CACHE_PREFIX = "product:search:";
    private static final long CACHE_TTL_MINUTES = 30;


    @Override
    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(key = "'product:add:' + #request.name", waitTime = 5000, leaseTime = 10000)
    public long addProduct(ProductCreationRequest request) {
        log.info("Add product {}", request);

        // Check if product with same name already exists
        String cacheKey = PRODUCT_CACHE_PREFIX + "name:" + request.getName();
        if (redissonCacheService.exists(cacheKey)) {
            log.warn("Product with name {} already exists", request.getName());
            throw new IllegalArgumentException("Product with this name already exists");
        }

        // save to RDMS
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUserId(request.getUserId());

        Product result = productRepository.save(product);

        // save to elasticsearch
        if (result.getId() != null) {
            ProductDocument productDocument = new ProductDocument();
            productDocument.setId(product.getId());
            productDocument.setName(request.getName());
            productDocument.setDescription(request.getDescription());
            productDocument.setPrice(request.getPrice());
            productDocument.setUserId(request.getUserId());

            productSearchRepository.save(productDocument);
            log.info("ProductDocument saved with id {}", productDocument.getId());
            
            // Cache the product
            String productCacheKey = PRODUCT_CACHE_PREFIX + result.getId();
            redissonCacheService.put(productCacheKey, productDocument, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            
            // Cache by name for duplicate check
            redissonCacheService.put(cacheKey, result.getId(), CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }

        return product.getId();
    }

    @Override

    @DistributedLock(key = "'product:update:' + #request.id", waitTime = 5000, leaseTime = 10000)
    public void updateProduct(ProductUpdateRequest request) {
        log.info("Update product {}", request);

        Product product = getProductById(request.getId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUserId(request.getUserId());

        productRepository.save(product);

        if (product.getId() != null) {
            ProductDocument productDocument = getProductDocumentById(request.getId());
            productDocument.setId(product.getId());
            productDocument.setName(request.getName());
            productDocument.setDescription(request.getDescription());
            productDocument.setPrice(request.getPrice());
            productDocument.setUserId(request.getUserId());

            productSearchRepository.save(productDocument);

            log.info("Update productDocument", productDocument);
            
            // Update cache
            String productCacheKey = PRODUCT_CACHE_PREFIX + product.getId();
            redissonCacheService.put(productCacheKey, productDocument, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            
            // Clear search cache as product name might have changed
            clearSearchCache();
        }
    }

    @Override
    @DistributedLock(key = "'product:delete:' + #productId", waitTime = 5000, leaseTime = 10000)
    public void deleteProduct(long productId) {
        log.info("Delete product with id: {}", productId);
        
        // Get product info before deletion for cache cleanup
        ProductDocument productDocument = getProductDocumentById(productId);
        
        productRepository.deleteById(productId);
        productSearchRepository.deleteById(productId);
        
        // Clear cache
        String productCacheKey = PRODUCT_CACHE_PREFIX + productId;
        redissonCacheService.delete(productCacheKey);
        
        // Clear name cache if exists
        String nameCacheKey = PRODUCT_CACHE_PREFIX + "name:" + productDocument.getName();
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
    public ProductDocument getProductById(Long id) {
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
    private Product getProductById(long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    /**
     * Get Product Document by id
     *
     * @param id
     * @return
     */
    private ProductDocument getProductDocumentById(long id) {
        return productSearchRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product document not found"));
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
