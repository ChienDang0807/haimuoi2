package vn.chiendt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "tbl_inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", unique = true, nullable = false)
    private Long productId;

    @Column(name = "available_quantity", nullable = false)
    private Long availableQuantity = 0L;

    @Column(name = "reserved_quantity", nullable = false)
    private Long reservedQuantity = 0L;

    @Column(name = "total_quantity", nullable = false)
    private Long totalQuantity = 0L;

    @CreationTimestamp
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Version
    private int version;
}
