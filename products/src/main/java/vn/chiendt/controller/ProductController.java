package vn.chiendt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.dto.request.ProductCreationRequest;
import vn.chiendt.dto.request.ProductUpdateRequest;
import vn.chiendt.dto.response.ApiResponse;
import vn.chiendt.service.ProductService;

@RestController
@RequestMapping("/product")
@Tag(name = "Product Controller")
@Slf4j(topic = "PRODUCT-CONTROLLER")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public ApiResponse getAllProducts(@RequestParam(required = false) String productName) {
        log.info("Get all products by {}", productName);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("product list")
                .data(productService.searchProduct(productName))
                .build();
    }

    @GetMapping("/{productId}")
    public ApiResponse getProductDetail(@PathVariable(required = false) Long productId) {
        log.info("Get product detail, id={}", productId);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("product list")
                .data(productService.getProductDocumentById(productId))
                .build();
    }

    @PostMapping("/add")
    public ApiResponse addProduct(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                  @RequestHeader(value = "X-Username", required = false) String username,
                                  @Valid @RequestBody ProductCreationRequest request) throws JsonProcessingException {
        log.info("Add new product");

        // Enforce user from gateway instead of client payload
        if (userId != null) {
            request.setUserId(userId);
        }

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Product created successfully")
                .data(productService.addProduct(request))
                .build();
    }

    @PutMapping("/upd")
    public ApiResponse updateProduct(@RequestBody ProductUpdateRequest request) throws  JsonProcessingException {
        log.info("Update product");

        productService.updateProduct(request);

        return ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Product updated successfully")
                .build();
    }

    @DeleteMapping("/del/{productId}")
    public ApiResponse deleteProduct(@PathVariable long productId) {
        log.info("Remove product: {}", productId);

        productService.deleteProduct(productId);

        return ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Product deleted successfully")
                .build();
    }
}
