package vn.chiendt.dto.response;

import lombok.*;
import vn.chiendt.common.TransactionType;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionResponse implements Serializable {
    private Long id;
    private Long productId;
    private Long quantity;
    private TransactionType type;
    private Long referenceId;
    private Date createdAt;
    private Date updatedAt;

}
