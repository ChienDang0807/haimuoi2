package vn.chiendt.repository.criteriasearch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import vn.chiendt.dto.request.AdvanceSearchRequest;
import vn.chiendt.dto.request.SearchField;
import vn.chiendt.dto.response.PageResponse;
import vn.chiendt.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j(topic = "ADVANCE-SEARCH-REPOSITORY")
public class AdvanceSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public PageResponse<?> advanceSearch(AdvanceSearchRequest request) {
        log.info("advanceSearch call");

        List<SearchCriteria> searchCriteria = new ArrayList<>();
        for (SearchField<?> field : request.getSearchFields()){
            searchCriteria.add(new SearchCriteria(field.getField(), field.getOperation(),field.getValue()));
        }

        List<Transaction> transactions = executeQuery(searchCriteria, request.getSort(), request.getPage(), request.getSize());

        Long totalElement = getTotalElements(searchCriteria);

        Page<Transaction> page = new PageImpl<>(transactions, PageRequest.of(request.getPage(), request.getSize()), totalElement);
        return PageResponse.builder().build();
    }

    private Long getTotalElements(List<SearchCriteria> searchCriteria) {
        log.info("getTotalElements call");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Transaction> root = query.from(Transaction.class);

        Predicate predicate = criteriaBuilder.conjunction();
        SearchQueryCriteriaConsumer<Transaction> consumer = new SearchQueryCriteriaConsumer<>(predicate, criteriaBuilder, root);
        searchCriteria.forEach(consumer);
        predicate = consumer.getPredicate();
        query.select(criteriaBuilder.count(root));
        query.where(predicate);

        return entityManager.createQuery(query).getSingleResult();
    }

    private List<Transaction> executeQuery(List<SearchCriteria> searchCriteria, String sort, int offset, int limit) {
        log.info("executeQuery call");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = criteriaBuilder.createQuery(Transaction.class);
        Root<Transaction> root = query.from(Transaction.class);

        Predicate predicate = criteriaBuilder.conjunction();
        SearchQueryCriteriaConsumer<Transaction> consumer = new SearchQueryCriteriaConsumer<>(predicate, criteriaBuilder, root);
        searchCriteria.forEach(consumer);
        predicate = consumer.getPredicate();
        query.where(predicate);

        Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
        if(StringUtils.hasLength(sort)){
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()){
                String fieldName = matcher.group(1);
                String direction = matcher.group(3);
                if ("asc".equalsIgnoreCase(direction)) {
                    query.orderBy(criteriaBuilder.asc(root.get(fieldName)));
                }else {
                    query.orderBy(criteriaBuilder.desc(root.get(fieldName)));
                }
            }
        }
        return  entityManager.createQuery(query).setFirstResult(offset).setMaxResults(limit).getResultList();
    }
}
