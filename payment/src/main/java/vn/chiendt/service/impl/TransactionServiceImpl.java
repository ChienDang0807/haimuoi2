package vn.chiendt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.chiendt.common.TransactionStatus;
import vn.chiendt.common.PaymentProvider;
import vn.chiendt.dto.request.AdvanceSearchRequest;
import vn.chiendt.dto.response.PageResponse;
import vn.chiendt.dto.response.TransactionResponse;
import vn.chiendt.exception.ResourceNotFoundException;
import vn.chiendt.model.Transaction;
import vn.chiendt.repository.TransactionRepository;
import vn.chiendt.repository.criteriasearch.AdvanceSearchRepository;
import vn.chiendt.service.TransactionService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "TRANSACTION-SERVICE")
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AdvanceSearchRepository advanceSearchRepository;

    @Override
    public PageResponse getAllTransactions(String keyword, String sort, int page, int size) {
        log.info("getAllTransactions called");

        // Sorting
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); // column:asc|desc
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = new Sort.Order(Sort.Direction.ASC, columnName);
                } else {
                    order = new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }

        // Xu ly truong hop FE muon bat dau voi page = 1
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }

        // Paging
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        Page<Transaction> entityPage;

        if (StringUtils.hasLength(keyword)) {
            keyword = "%" + keyword + "%";
            entityPage = transactionRepository.searchTransactionByKeyword(keyword, pageable);
        } else {
            entityPage = transactionRepository.findAll(pageable);
        }

        return getPageResponse(page, size, entityPage);
    }

    @Override
    public PageResponse<?> advanceSearch(AdvanceSearchRequest request) {
        log.info("advanceSearch called");
        request.validate();
        return advanceSearchRepository.advanceSearch(request);
    }

    @Override
    public TransactionResponse getTransactionDetail(Long id) {
        log.info("getTransactionDetail called");

        Transaction transaction = getTransactionById(id);

        return TransactionResponse.builder()
                .id(transaction.getId())
                .customerId(transaction.getCustomerId())
                .paymentId(transaction.getPaymentId())
                .paymentMethod(transaction.getPaymentMethod())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    @Override
    public String getOrderId(String paymentId) {
        log.info("getOrderId called");

        Transaction transaction = transactionRepository.findTransactionByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with paymentId: " + paymentId));

        return transaction.getOrderId();
    }

    @Override
    public String getOrderId(PaymentProvider provider, String providerPaymentId) {
        log.info("getOrderId by provider called");

        Transaction transaction = transactionRepository
                .findByPaymentProviderAndProviderPaymentId(provider, providerPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with provider: " + provider + ", providerPaymentId: " + providerPaymentId));

        return transaction.getOrderId();
    }

    @Override
    public Long createTransaction(Transaction transaction) {
        log.info("createTransaction called");

        Transaction result = transactionRepository.save(transaction);

        return result.getId();
    }

    @Override
    public void updateTransactionStatus(String paymentId, TransactionStatus status) {
        log.info("Updating transaction with paymentId {} and status {}", paymentId, status);

        Transaction transaction = transactionRepository.findTransactionByPaymentId(paymentId).orElseThrow(()->new ResourceNotFoundException("Transaction not found"));
        transaction.setStatus(status);

        transactionRepository.save(transaction);
    }

    @Override
    public void updateTransactionStatus(PaymentProvider provider, String providerPaymentId, TransactionStatus status) {
        log.info("Updating transaction with provider {} providerPaymentId {} and status {}", provider, providerPaymentId, status);

        Transaction transaction = transactionRepository
                .findByPaymentProviderAndProviderPaymentId(provider, providerPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        transaction.setStatus(status);

        transactionRepository.save(transaction);
    }

    private Transaction getTransactionById(Long id) {
        log.info("getTransactionById called");
        return transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    /**
     * Convert transaction entity to DTO
     *
     * @param page
     * @param size
     * @param transactionPage
     * @return
     */
    private static PageResponse<?> getPageResponse(int page, int size, Page<Transaction> transactionPage) {
        log.info("getPageResponse called");

        List<TransactionResponse> transactions = transactionPage.stream().map(entity -> TransactionResponse.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .paymentId(entity.getPaymentId())
                .paymentMethod(entity.getPaymentMethod())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build()
        ).toList();

        return PageResponse.builder()
                .pageNumber(page)
                .pageSize(size)
                .totalPages(transactionPage.getTotalPages())
                .totalElements(transactionPage.getTotalElements())
                .transactions(transactions)
                .build();
    }
}
