package vn.chiendt.dto.request;

import lombok.Getter;
import vn.chiendt.common.Operation;

@Getter
public class SearchField<T> {
    private String field;
    private Operation operation;
    private T value;
}
