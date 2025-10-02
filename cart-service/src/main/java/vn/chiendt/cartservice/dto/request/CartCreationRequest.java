package vn.chiendt.cartservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.chiendt.cartservice.common.CartState;



import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CartCreationRequest {

    @NotNull(message = "UserId is required")
    private Long userId;

    private CartState cartState;

    private Integer quantity;

    @Valid
    private List<CartItemRequest> cartItems = new ArrayList<>();


}
