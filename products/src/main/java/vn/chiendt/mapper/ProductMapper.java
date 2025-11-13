package vn.chiendt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.chiendt.dto.response.ProductResponse;
import vn.chiendt.model.Product;
import vn.chiendt.model.ProductDocument;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "userId", ignore = true)
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "userId", ignore = true)
    ProductResponse toProductResponseFromProductDoc(ProductDocument productDocument);
}
