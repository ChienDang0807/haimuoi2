package vn.chiendt.repository.criteriasearch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.chiendt.common.Operation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String columnName;
    private Operation operation;
    private Object value;
}
