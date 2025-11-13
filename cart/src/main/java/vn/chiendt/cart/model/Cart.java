package vn.chiendt.cart.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.chiendt.cart.common.CartState;
import vn.chiendt.cart.common.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import vn.chiendt.cart.model.CartItem;

@Entity
@Getter
@Setter
@Table(name = "tbl_cart")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    private String id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "cart_token")
    private String cartToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "cart_state", length = 20, nullable = false)
    private CartState cartState;

    private BigDecimal shippingFee = BigDecimal.valueOf(30000); // auto

    private BigDecimal discount;

    @CreationTimestamp
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Instant updatedAt;

    @Transient
    private List<CartItem> cartItems;

}
