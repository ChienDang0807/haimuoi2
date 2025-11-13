package vn.chiendt.dto.response;


import lombok.Getter;
import lombok.Setter;


import java.util.Date;

@Getter
@Setter
public class InventoryItemResponse {

    private Long id;
    private Long productId;
    private Integer availableQuantity = 0;
    private Integer reservedQuantity = 0;
    private Integer totalQuantity = 0;

    private Date createdAt;
    private Date updatedAt;

}
