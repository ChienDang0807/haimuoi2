package vn.chiendt.dto.response;

import lombok.Getter;
import lombok.Setter;
import vn.chiendt.common.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long userId;
    private String slug;
    private ProductStatus status;
    private Map<String, String> attributes;
    private Instant createdAt;
    private Instant updatedAt;
}
