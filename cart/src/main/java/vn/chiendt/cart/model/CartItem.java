package vn.chiendt.cart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Entity
@Getter
@Setter
@Builder
@Table(name = "tbl_cart_item")
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id" ,nullable = false)
    private Long productId;

    @Column(name = "cart_identifier" , nullable = false)
    private String cartIdentifier;

    @Column(name = "product_name" , nullable = false)
    private String productName;

    @Column(name = "price",nullable = false, precision = 10 , scale = 2)
    private BigDecimal price;

    @Column(name = "quantity")
    private  Integer quantity;

    @Column(precision = 10 ,scale = 2)
    private BigDecimal discount;

    private String unit;

    private String shopId;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default // giữ giá trị default khi khởi tạo object
    private Map<String, String> attributes = new HashMap<>();

    public CartItem(Long id, Long productId, String productName, BigDecimal price, Integer quantity, BigDecimal discount, String unit, String shopId, Map<String, String> attributes) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.unit = unit;
        this.shopId = shopId;
        this.attributes = attributes;
    }
}
