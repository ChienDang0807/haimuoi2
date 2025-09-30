package vn.chiendt.service;

import vn.chiendt.dto.request.InventoryTransactionCreationRequest;
import vn.chiendt.dto.request.InventoryTransactionUpdateRequest;
import vn.chiendt.dto.response.InventoryTransactionResponse;
import vn.chiendt.dto.response.PageResponse;

public interface InventoryTransactionService {
    PageResponse<?> getAllInventoryTransaction(String sort, int page, int size);

    InventoryTransactionResponse getInventoryTransactionDetail(Long id);

    Long addInventoryTransaction(InventoryTransactionCreationRequest request);

    void updateInventoryTransaction(InventoryTransactionUpdateRequest request);

    void deleteInventoryTransaction(Long id);
}
