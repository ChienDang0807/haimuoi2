package vn.chiendt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chiendt.dto.request.InventoryTransactionCreationRequest;
import vn.chiendt.dto.request.InventoryTransactionUpdateRequest;
import vn.chiendt.dto.response.InventoryTransactionResponse;
import vn.chiendt.dto.response.PageResponse;
import vn.chiendt.exception.ResourceNotFoundException;
import vn.chiendt.model.InventoryTransaction;
import vn.chiendt.repository.InventoryTransactionRepository;
import vn.chiendt.service.InventoryTransactionService;
import vn.chiendt.service.InventoryItemService;
import vn.chiendt.utils.PagingUtils;
import vn.chiendt.common.TransactionType;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j(topic = "INVENTORY-TRANSACTION-SERVICE")
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryItemService inventoryItemService;

    /**
     * Get all inventories
     *
     * @param sort
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResponse<?> getAllInventoryTransaction(String sort, int page, int size) {
        log.info("getAllInventoryTransaction called");

        // Sorting
        Sort.Order order = PagingUtils.buildSortOrder(sort, "id");
        int pageNo = PagingUtils.normalizePage(page);


        // Paging
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        Page<InventoryTransaction> entityPage = inventoryTransactionRepository.findAll(pageable);

        return getPageResponse(page, size, entityPage);
    }

    /**
     * Get inventory by id
     *
     * @param id
     * @return
     */
    @Override
    public InventoryTransactionResponse getInventoryTransactionDetail(Long id) {
        log.info("getInventoryTransactionDetail called");

        InventoryTransaction inventory = getInventoryTransactionById(id);

        return InventoryTransactionResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .type(inventory.getType())
                .referenceId(inventory.getReferenceId())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    /**
     * Create inventory
     *
     * @param request
     * @return
     */
    @Override

    public Long addInventoryTransaction(InventoryTransactionCreationRequest request) {
        log.info("addInventoryTransaction called");

        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setProductId(request.getProductId());
        inventoryTransaction.setQuantity(request.getQuantity());
        inventoryTransaction.setType(request.getType());
        inventoryTransaction.setReferenceId(request.getReferenceId());

        InventoryTransaction result = inventoryTransactionRepository.save(inventoryTransaction);
        log.info("Inventory transaction created");

        // If this is an initial stock transaction, initialize the inventory item
        if (request.getType() == TransactionType.INITIAL_STOCK) {
            inventoryItemService.initializeInventory(request.getProductId(), request.getQuantity());
            log.info("Inventory item initialized for product ID: {}", request.getProductId());
        }

        return result.getId();
    }

    /**
     * Initialize inventory for a new product
     * @param productId the product ID
     * @param initialQuantity the initial stock quantity
     * @return the transaction ID
     */

    public Long initializeProductInventory(Long productId, Long initialQuantity) {
        log.info("Initializing inventory for product ID: {} with quantity: {}", productId, initialQuantity);

        InventoryTransactionCreationRequest request = new InventoryTransactionCreationRequest();
        request.setProductId(productId);
        request.setQuantity(initialQuantity);
        request.setType(TransactionType.INITIAL_STOCK);
        request.setReferenceId(productId); // Use product ID as reference for initial stock

        return addInventoryTransaction(request);
    }

    /**
     * Update inventory
     *
     * @param request
     */
    @Override
    @Transactional
    public void updateInventoryTransaction(InventoryTransactionUpdateRequest request) {
        log.info("updateInventory called");

        InventoryTransaction inventoryTransaction = getInventoryTransactionById(request.getId());
        inventoryTransaction.setProductId(request.getProductId());
        inventoryTransaction.setQuantity(request.getQuantity());
        inventoryTransaction.setType(request.getType());
        inventoryTransaction.setReferenceId(request.getReferenceId());

        inventoryTransactionRepository.save(inventoryTransaction);
        log.info("Inventory transaction updated");
    }

    /**
     * Delete inventory by id
     *
     * @param id
     */
    @Override
    public void deleteInventoryTransaction(Long id) {
        log.info("deleteInventoryTransaction called");
        inventoryTransactionRepository.deleteById(id);
    }

    /**
     * Get inventory by product id
     *
     * @param productId
     * @return InventoryTransaction
     */
    private InventoryTransaction getInventoryTransactionByProductId(Long productId) {
        log.info("getInventoryTransactionByProductId called");
        return inventoryTransactionRepository.findByProductId(productId).orElseThrow(() -> new ResourceNotFoundException("InventoryTransaction not found with productId " + productId));
    }

    /**
     * Get inventory by inventory id
     *
     * @param inventoryById
     * @return InventoryTransaction
     */
    private InventoryTransaction getInventoryTransactionById(Long inventoryById) {
        log.info("getInventoryTransactionById called");
        return inventoryTransactionRepository.findById(inventoryById).orElseThrow(() -> new ResourceNotFoundException("InventoryTransaction not found with id " + inventoryById));
    }

    private InventoryTransaction getInventoryTransactionByIdPessimisticLocked(Long inventoryTransactionId) {
        log.info("getInventoryTransactionByIdPessimisticLocked called");
        return inventoryTransactionRepository.findByIdAndLock(inventoryTransactionId).orElseThrow(() -> new ResourceNotFoundException("InventoryTransaction not found with id " + inventoryTransactionId));
    }

    /**
     * Convert transaction entity to DTO
     *
     * @param page
     * @param size
     * @param transactionPage
     * @return
     */
    private static PageResponse<?> getPageResponse(int page, int size, Page<InventoryTransaction> transactionPage) {
        log.info("getPageResponse called");

        List<InventoryTransactionResponse> inventories = transactionPage.stream().map(inventory -> InventoryTransactionResponse.builder()
                .id(inventory.getId())
                .quantity(inventory.getQuantity())
                .type(inventory.getType())
                .referenceId(inventory.getReferenceId())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build()
        ).toList();

        return PageResponse.builder()
                .pageNumber(page)
                .pageSize(size)
                .totalPages(transactionPage.getTotalPages())
                .totalElements(transactionPage.getTotalElements())
                .data(inventories)
                .build();
    }
}
