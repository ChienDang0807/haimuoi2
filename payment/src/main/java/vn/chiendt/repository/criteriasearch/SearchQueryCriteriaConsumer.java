package vn.chiendt.repository.criteriasearch;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.chiendt.common.Operation;

import java.util.function.Consumer;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryCriteriaConsumer<T> implements Consumer<SearchCriteria> {
    private Predicate predicate;
    private CriteriaBuilder builder;
    private Root<T> root;

    @Override
    public void accept(SearchCriteria param) {
        if (param.getOperation().equals(Operation.GREATER_THAN)) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get(param.getColumnName()), param.getValue().toString()));
        } else if (param.getOperation().equals(Operation.LESS_THAN)) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get(param.getColumnName()), param.getValue().toString()));
        } else if (param.getOperation().equals(Operation.EQUALITY)) {
            if (root.get(param.getColumnName()).getJavaType() == String.class) {
                predicate = builder.and(predicate, builder.like(root.get(param.getColumnName()), "%" + param.getValue() + "%"));
            } else {
                predicate = builder.and(predicate, builder.equal(root.get(param.getColumnName()), param.getValue()));
            }
        }
    }
}
