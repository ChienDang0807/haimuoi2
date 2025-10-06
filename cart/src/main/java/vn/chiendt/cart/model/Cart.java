package vn.chiendt.cart.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import vn.chiendt.cart.common.CartState;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "cart")
public class Cart {
    @Id
    private String id;

    private Long userId;

    private CartState cartState;

    private Integer quantity;

    private List<CartItem> cartItems = new ArrayList<>();

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;
}
