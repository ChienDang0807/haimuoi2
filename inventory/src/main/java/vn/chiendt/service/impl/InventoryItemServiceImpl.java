package vn.chiendt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chiendt.dto.response.InventoryItemResponse;
import vn.chiendt.mapper.InventoryItemMapper;
import vn.chiendt.model.InventoryItem;
import vn.chiendt.repository.InventoryItemRepository;
import vn.chiendt.service.InventoryItemService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "INVENTORY-ITEM-SERVICE")
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemMapper inventoryItemMapper;

    @Override
    @Transactional
    public InventoryItem initializeInventory(Long productId, Integer initialQuantity) {
        log.info("Initializing inventory for product ID: {} with quantity: {}", productId, initialQuantity);
        
        // Check if inventory already exists for this product
        if (inventoryItemRepository.existsByProductId(productId)) {
            log.warn("Inventory already exists for product ID: {}", productId);
            throw new IllegalArgumentException("Inventory already exists for product ID: " + productId);
        }
        
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setProductId(productId);
        inventoryItem.setAvailableQuantity(initialQuantity);
        inventoryItem.setReservedQuantity(0);
        inventoryItem.setTotalQuantity(initialQuantity);
        
        InventoryItem result = inventoryItemRepository.save(inventoryItem);
        log.info("Inventory initialized for product ID: {} with ID: {}", productId, result.getId());
        
        return result;
    }

    @Override
    public InventoryItemResponse getInventoryByProductId(Long productId) {
        log.info("Getting inventory for product ID: {}", productId);
        
        InventoryItem inventoryItem = findInventoryItemByProductId(productId);
        return inventoryItemMapper.toInventoryItemResponse(inventoryItem);
    }

    @Override
    @Transactional
    public InventoryItemResponse updateAvailableQuantity(Long productId, Integer quantity, boolean isAddition) {
        log.info("Updating available quantity for product ID: {} by {} (addition: {})", productId, quantity, isAddition);

        InventoryItem inventoryItem = findInventoryItemByProductId(productId);
        
        if (isAddition) {
            inventoryItem.setAvailableQuantity(inventoryItem.getAvailableQuantity() + quantity);
            inventoryItem.setTotalQuantity(inventoryItem.getTotalQuantity() + quantity);
        } else {
            if (inventoryItem.getAvailableQuantity() < quantity) {
                throw new IllegalArgumentException("Insufficient available quantity for product ID: " + productId);
            }
            inventoryItem.setAvailableQuantity(inventoryItem.getAvailableQuantity() - quantity);
            inventoryItem.setTotalQuantity(inventoryItem.getTotalQuantity() - quantity);
        }
        
        InventoryItem result = inventoryItemRepository.save(inventoryItem);
        log.info("Available quantity updated for product ID: {}", productId);
        
        return inventoryItemMapper.toInventoryItemResponse(result);
    }

    @Override
    @Transactional
    public InventoryItemResponse reserveQuantity(Long productId, Integer quantity) {
        log.info("Reserving quantity {} for product ID: {}", quantity, productId);

        InventoryItem inventoryItem = findInventoryItemByProductId(productId);
        
        if (inventoryItem.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient available quantity for reservation. Product ID: " + productId);
        }
        
        inventoryItem.setAvailableQuantity(inventoryItem.getAvailableQuantity() - quantity);
        inventoryItem.setReservedQuantity(inventoryItem.getReservedQuantity() + quantity);
        
        InventoryItem result = inventoryItemRepository.save(inventoryItem);
        log.info("Quantity reserved for product ID: {}", productId);
        
        return inventoryItemMapper.toInventoryItemResponse(result);
    }

    @Override
    @Transactional
    public InventoryItemResponse releaseReservedQuantity(Long productId, Integer quantity) {
        log.info("Releasing reserved quantity {} for product ID: {}", quantity, productId);

        InventoryItem inventoryItem = findInventoryItemByProductId(productId);
        
        if (inventoryItem.getReservedQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient reserved quantity for release. Product ID: " + productId);
        }
        
        inventoryItem.setReservedQuantity(inventoryItem.getReservedQuantity() - quantity);
        inventoryItem.setAvailableQuantity(inventoryItem.getAvailableQuantity() + quantity);
        
        InventoryItem result = inventoryItemRepository.save(inventoryItem);
        log.info("Reserved quantity released for product ID: {}", productId);
        
        return inventoryItemMapper.toInventoryItemResponse(result);
    }

    @Override
    public Boolean isInventoryItemAvailable(Long productId, Integer quantity) {
        Integer availableQuantity = inventoryItemRepository.findAvailableQuantityByProductId(productId);
        return availableQuantity != null && availableQuantity >= quantity;
    }

    private InventoryItem findInventoryItemByProductId(Long productId) {
        return inventoryItemRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product ID: " + productId));
    }
}
