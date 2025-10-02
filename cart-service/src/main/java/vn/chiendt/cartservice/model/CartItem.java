package vn.chiendt.cartservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class CartItem {
    private Long productId;
    private String productName;
    private Double price;
    private  Integer quantity;
    private String shopId;


    private Double discount;
}
