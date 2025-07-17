package vn.chiendt.dto.response;

import lombok.*;
import vn.chiendt.common.Currency;
import vn.chiendt.common.PaymentMethod;
import vn.chiendt.common.TransactionStatus;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse implements Serializable {
    private Long id;
    private Long customerId;
    private String paymentId;
    private PaymentMethod paymentMethod;
    private Long amount;
    private Currency currency;
    private String description;
    private TransactionStatus status;
    private Date createdAt;
    private Date updatedAt;
}
