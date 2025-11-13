package vn.chiendt.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.chiendt.common.TransactionType;

import java.io.Serializable;

@Getter
@Setter
public class InventoryTransactionCreationRequest implements Serializable {

    @NotNull(message = "productId must be not null")
    private Long productId;

    @NotNull(message = "quantity must be not null")
    private Integer quantity;

    @NotNull(message = "type must be not null")
    private TransactionType type;

    @NotNull(message = "referenceId must be not null")
    private Long referenceId;
}
