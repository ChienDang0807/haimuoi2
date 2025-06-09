package vn.chiendt.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.chiendt.model.Order;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderListResponse {
    private int pageNumber;
    private int pageSize;
    private long totalPages;
    private long totalElements;
    private List<Order> orders;
}
