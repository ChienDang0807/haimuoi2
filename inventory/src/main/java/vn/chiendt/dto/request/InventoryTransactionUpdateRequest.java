package vn.chiendt.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import vn.chiendt.common.TransactionType;

import java.io.Serializable;

@Getter
@ToString
public class InventoryTransactionUpdateRequest implements Serializable {
    @NotNull(message = "Id must be not null")
    private Long Id;

    @NotNull(message = "productId must be not null")
    private Long productId;

    @NotNull(message = "quantity must be not null")
    private Long quantity;

    @NotNull(message = "type must be not null")
    private TransactionType type;

    @NotNull(message = "referenceId must be not null")
    private Long referenceId;
}
