package vn.chiendt.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.chiendt.common.Currency;
import vn.chiendt.common.PaymentMethod;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "order")
public class Order {
    @Id
    private String id;

    private Long customerId;

    private Long amount;

    private Currency currency;

    // @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    private int status;

    private String statusName;

    private Date createdAt;

    private Date updatedAt;

    private List<OrderItem> orderItems;
}
