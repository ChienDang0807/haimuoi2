package vn.chiendt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "product-image")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductImage {

    @Id
    private String id;

    private Long productId;

    private String imageUrl;

    private String thumbnailUrl;

    private Integer width;

    private Integer height;

    private Long fileSize;


}
