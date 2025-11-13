package vn.chiendt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import vn.chiendt.dto.request.ProductCreationRequest;
import vn.chiendt.dto.request.ProductUpdateRequest;
import vn.chiendt.dto.response.ProductResponse;
import vn.chiendt.model.ProductDocument;

import java.util.List;

public interface ProductService {
    List<ProductDocument> searchProduct(String name);

    ProductResponse getProductDocumentById(Long id);

    ProductResponse addProduct(ProductCreationRequest request) throws JsonProcessingException;

    void updateProduct(ProductUpdateRequest product) throws JsonProcessingException;

    void deleteProduct(long productId);
}
