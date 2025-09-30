package vn.chiendt.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class SaleOrderItemResponse {
    private Long id;
    private Long salesId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private Date createdAt;
    private Date updatedAt;
}
