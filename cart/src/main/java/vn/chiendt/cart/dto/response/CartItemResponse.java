package vn.chiendt.cart.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartItemResponse {

    private Long productId;
    private String productName;
    private Long price;
    private Integer quantity;
    private String unit;
    private String shopId;
    private Long discount;
    private Long subtotal;
    private String productImage;
    private String productCategory;
    private Boolean isAvailable;
    private Integer stockQuantity;
    private String productDescription;
    private List<String> productTags;
}
