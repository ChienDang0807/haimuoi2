package vn.chiendt.cart.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CartItemResponse {

    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String unit;
    private String shopId;
    private BigDecimal discount;
    private BigDecimal subTotal;
    private String productImage;
    private String productCategory;
    private Boolean isAvailable;
    private Integer stockQuantity;
    private String productDescription;
    private Map<String, String> productAttributes = new HashMap<>();
    private List<String> productTags;
}
