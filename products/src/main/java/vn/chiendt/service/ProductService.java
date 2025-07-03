package vn.chiendt.service;

import vn.chiendt.dto.request.ProductCreationRequest;
import vn.chiendt.dto.request.ProductUpdateRequest;
import vn.chiendt.model.ProductDocument;

import java.util.List;

public interface ProductService {
    List<ProductDocument> searchProduct(String name);

    ProductDocument getProductById(Long id);

    long addProduct(ProductCreationRequest request);

    void updateProduct(ProductUpdateRequest product);

    void deleteProduct(long productId);
}
