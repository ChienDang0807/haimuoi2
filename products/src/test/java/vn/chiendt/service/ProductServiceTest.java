package vn.chiendt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import vn.chiendt.avro.ProductEvent;
import vn.chiendt.cache.redisson.service.RedissonCacheService;
import vn.chiendt.common.ProductStatus;
import vn.chiendt.dto.request.ProductCreationRequest;
import vn.chiendt.dto.response.ProductResponse;
import vn.chiendt.mapper.ProductMapper;
import vn.chiendt.model.Product;
import vn.chiendt.repository.ProductRepository;
import vn.chiendt.service.impl.ProductServiceImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private RedissonCacheService redissonCacheService;
    @Mock private ProductRepository productRepository;
    @Mock private KafkaTemplate<String, ProductEvent> kafkaTemplate;
    @Mock private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        // Set the kafka topic name using ReflectionTestUtils
        ReflectionTestUtils.setField(productService, "productSyncEvents", "product-sync-events");
    }

    @Test
    void addProduct_shouldThrowException_whenProductExistsInCache() {
        ProductCreationRequest request = new ProductCreationRequest();
        request.setName("iPhone 16");
        request.setPrice(BigDecimal.TEN);
        request.setDescription("Xin xo");
        request.setUserId(1L);
        request.setAttributes(Map.of("color", "black"));

        // Fix: Use the correct cache key that matches the service implementation
        when(redissonCacheService.exists("product:name:iPhone 16")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(request));

        verify(redissonCacheService).exists("product:name:iPhone 16");
        verifyNoInteractions(productRepository, kafkaTemplate);
    }

    @Test
    void addProduct_shouldSaveProductAndSendKafkaEvent() throws Exception {
        ProductCreationRequest request = new ProductCreationRequest();
        request.setName("Macbook Pro");
        request.setUserId(1L);
        request.setPrice(BigDecimal.valueOf(2000));
        request.setDescription("High-end laptop");
        request.setAttributes(Map.of("brand", "Apple"));

        // Create a properly initialized Product with timestamps
        Product saved = new Product();
        saved.setId(10L);
        saved.setName("Macbook Pro");
        saved.setSlug("macbook-pro");
        saved.setPrice(BigDecimal.valueOf(2000));
        saved.setStatus(ProductStatus.ACTIVE);
        saved.setUserId(1L);
        saved.setDescription("High-end laptop");
        saved.setAttributes(Map.of("brand", "Apple"));
        // Fix: Set timestamps to avoid NullPointerException
        saved.setCreatedAt(Instant.now());
        saved.setUpdatedAt(Instant.now());

        when(redissonCacheService.exists("product:name:Macbook Pro")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(saved);
        when(productRepository.existsBySlug(anyString())).thenReturn(false);
        when(productMapper.toProductResponse(any())).thenReturn(new ProductResponse());

        ProductResponse response = productService.addProduct(request);

        verify(productRepository, times(2)).save(any(Product.class)); // 2 lần: before và after slug
        verify(kafkaTemplate).send(eq("product-sync-events"), any(ProductEvent.class));
        verify(redissonCacheService, atLeastOnce()).put(anyString(), any(), anyLong(), any());
        assertNotNull(response);
    }
}