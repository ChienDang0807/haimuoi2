package vn.chiendt.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.chiendt.cart.dto.response.CartResponse;
import vn.chiendt.cart.model.Cart;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper {
    
    @Mapping(target = "cartItemResponses", source = "cartItems")
    @Mapping(target = "quantity", expression = "java(calculateTotalQuantity(cart))")
    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(cart))")
    CartResponse toResponse(Cart cart);
    
    default Integer calculateTotalQuantity(Cart cart) {
        if (cart.getCartItems() == null) {
            return 0;
        }
        return cart.getCartItems().stream()
                .mapToInt(CartItem -> CartItem.getQuantity())
                .sum();
    }
    
    default java.math.BigDecimal calculateTotalAmount(Cart cart) {
        if (cart.getCartItems() == null) {
            return java.math.BigDecimal.ZERO;
        }
        return cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                .add(cart.getShippingFee() != null ? cart.getShippingFee() : java.math.BigDecimal.ZERO)
                .subtract(cart.getDiscount() != null ? cart.getDiscount() : java.math.BigDecimal.ZERO);
    }
}
