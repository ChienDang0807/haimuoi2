package vn.chiendt.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.chiendt.cart.dto.response.CartItemResponse;
import vn.chiendt.cart.model.CartItem;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    
    @Mapping(target = "subTotal", expression = "java(calculateSubTotal(item))")
    @Mapping(target = "productAttributes", source = "attributes")
    CartItemResponse toResponse(CartItem item);
    
    default java.math.BigDecimal calculateSubTotal(CartItem item) {
        return item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity()));
    }
}
