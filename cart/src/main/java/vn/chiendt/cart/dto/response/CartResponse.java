package vn.chiendt.cart.dto.response;

import lombok.Builder;
import lombok.Getter;
import vn.chiendt.cart.common.CartState;
import vn.chiendt.cart.common.Currency;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CartResponse {

    private String id;

    private Long userId;

    private String cartToken;

    private CartState cartState;

    private Currency currency;

    private Integer quantity;

    private BigDecimal totalAmount;

    private List<CartItemResponse> cartItemResponses ;

}
