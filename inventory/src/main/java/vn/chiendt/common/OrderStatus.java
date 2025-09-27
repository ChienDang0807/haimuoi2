package vn.chiendt.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    NEW(1),
    PENDING(2),
    CANCELED(3),
    PAID(4),
    FAILED(5),
    IN_PROGRESS(6),
    DELIVERED(7),
    CLOSED(8);

    private final int value;
}
