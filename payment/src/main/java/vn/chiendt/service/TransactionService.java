package vn.chiendt.service;

import vn.chiendt.common.TransactionStatus;
import vn.chiendt.dto.request.AdvanceSearchRequest;
import vn.chiendt.dto.response.PageResponse;
import vn.chiendt.dto.response.TransactionResponse;
import vn.chiendt.model.Transaction;

public interface TransactionService {

    PageResponse getAllTransactions (String keyword, String sort, int page, int size);

    PageResponse advanceSearch (AdvanceSearchRequest request);

    TransactionResponse getTransactionDetail(Long id);

    String getOrderId(String paymentId);

    Long createTransaction(Transaction transaction);

    void updateTransactionStatus(String paymentId, TransactionStatus status);
}
