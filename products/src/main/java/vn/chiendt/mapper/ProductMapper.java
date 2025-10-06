package vn.chiendt.mapper;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import vn.chiendt.dto.response.ProductResponse;
import vn.chiendt.model.Product;


@Component
public class ProductMapper {

    public  ProductResponse toProductResponse(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setUserId(product.getUserId());
        dto.setSlug(product.getSlug());
        dto.setStatus(product.getStatus());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        if (product.getAttributes() != null) {
            dto.setAttributes(product.getAttributes());
        }

        return dto;
    }
}
