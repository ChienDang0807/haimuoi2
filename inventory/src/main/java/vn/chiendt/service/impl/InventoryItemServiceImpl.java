package vn.chiendt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chiendt.model.InventoryItem;
import vn.chiendt.repository.InventoryItemRepository;
import vn.chiendt.service.InventoryItemService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "INVENTORY-ITEM-SERVICE")
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;

    @Override
    @Transactional
    public InventoryItem initializeInventory(Long productId, Long initialQuantity) {
        log.info("Initializing inventory for product ID: {} with quantity: {}", productId, initialQuantity);
        
        // Check if inventory already exists for this product
        if (inventoryItemRepository.existsByProductId(productId)) {
            log.warn("Inventory already exists for product ID: {}", productId);
            throw new IllegalArgumentException("Inventory already exists for product ID: " + productId);
        }
        
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setProductId(productId);
        inventoryItem.setAvailableQuantity(initialQuantity);
        inventoryItem.setReservedQuantity(0L);
        inventoryItem.setTotalQuantity(initialQuantity);
        
        InventoryItem result = inventoryItemRepository.save(inventoryItem);
        log.info("Inventory initialized for product ID: {} with ID: {}", productId, result.getId());
        
        return result;
    }

    @Override
    public InventoryItem getInventoryByProductId(Long productId) {
        log.info("Getting inventory for product ID: {}", productId);
        
        return inventoryItemRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product ID: " + productId));
    }

    @Override
    @Transactional
    public InventoryItem updateAvailableQuantity(Long productId, Long quantity, boolean isAddition) {
        log.info("Updating available quantity for product ID: {} by {} (addition: {})", productId, quantity, isAddition);
        
        InventoryItem inventoryItem = getInventoryByProductId(productId);
        
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
        
        return result;
    }

    @Override
    @Transactional
    public InventoryItem reserveQuantity(Long productId, Long quantity) {
        log.info("Reserving quantity {} for product ID: {}", quantity, productId);
        
        InventoryItem inventoryItem = getInventoryByProductId(productId);
        
        if (inventoryItem.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient available quantity for reservation. Product ID: " + productId);
        }
        
        inventoryItem.setAvailableQuantity(inventoryItem.getAvailableQuantity() - quantity);
        inventoryItem.setReservedQuantity(inventoryItem.getReservedQuantity() + quantity);
        
        InventoryItem result = inventoryItemRepository.save(inventoryItem);
        log.info("Quantity reserved for product ID: {}", productId);
        
        return result;
    }

    @Override
    @Transactional
    public InventoryItem releaseReservedQuantity(Long productId, Long quantity) {
        log.info("Releasing reserved quantity {} for product ID: {}", quantity, productId);
        
        InventoryItem inventoryItem = getInventoryByProductId(productId);
        
        if (inventoryItem.getReservedQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient reserved quantity for release. Product ID: " + productId);
        }
        
        inventoryItem.setReservedQuantity(inventoryItem.getReservedQuantity() - quantity);
        inventoryItem.setAvailableQuantity(inventoryItem.getAvailableQuantity() + quantity);
        
        InventoryItem result = inventoryItemRepository.save(inventoryItem);
        log.info("Reserved quantity released for product ID: {}", productId);
        
        return result;
    }
}
