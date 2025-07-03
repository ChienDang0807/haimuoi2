package vn.chiendt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
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

    private BigDecimal amount;

    private String currency;

    // @JsonProperty("payment_method")
    private String paymentMethod;

    private int status;

    private String statusName;

    private Date createdAt;

    private Date updatedAt;

    private List<OrderItem> orderItems;
}
