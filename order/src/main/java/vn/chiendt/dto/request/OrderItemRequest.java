package vn.chiendt.dto.request;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
public class OrderItemRequest implements Serializable {

    private Long productId;
    private String productName;
    private Integer quantity;
    private Long price;
    private String unit;
}
