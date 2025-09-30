package vn.chiendt.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.chiendt.common.OrderStatus;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
public class SaleOrderResponse implements Serializable {
    private String id;
    private Long customerId;
    private Date orderDate;
    private OrderStatus status;
    private Long totalAmount;
    private Date createdAt;
    private Date updatedAt;
}
