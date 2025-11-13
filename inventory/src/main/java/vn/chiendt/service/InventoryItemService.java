package vn.chiendt.service;

import vn.chiendt.dto.response.InventoryItemResponse;
import vn.chiendt.model.InventoryItem;

public interface InventoryItemService {
    
    /**
     * Initialize inventory for a new product
     * @param productId the product ID
     * @param initialQuantity the initial stock quantity
     * @return the created inventory item
     */
    InventoryItem initializeInventory(Long productId, Integer initialQuantity);
    
    /**
     * Get inventory item by product ID
     * @param productId the product ID
     * @return the inventory item
     */
    InventoryItemResponse getInventoryByProductId(Long productId);
    
    /**
     * Update available quantity
     * @param productId the product ID
     * @param quantity the quantity to add/subtract
     * @param isAddition true for addition, false for subtraction
     * @return the updated inventory item
     */
    InventoryItemResponse updateAvailableQuantity(Long productId, Integer quantity, boolean isAddition);
    
    /**
     * Reserve quantity for an order
     * @param productId the product ID
     * @param quantity the quantity to reserve
     * @return the updated inventory item
     */
    InventoryItemResponse reserveQuantity(Long productId, Integer quantity);
    
    /**
     * Release reserved quantity
     * @param productId the product ID
     * @param quantity the quantity to release
     * @return the updated inventory item
     */
    InventoryItemResponse releaseReservedQuantity(Long productId, Integer quantity);

    /**
     * kiem tra ton kho san pham
     * @param productId the product ID
     * @param quantity the quantity when customer buy
     * @return
     */
    Boolean isInventoryItemAvailable (Long productId, Integer quantity);
}
