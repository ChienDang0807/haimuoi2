package vn.chiendt.cart.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponse {

    private Long productId;


    private String productName;


    private Double price;

    private Integer quantity;

    private String shopId;

    private Double discount;

}
