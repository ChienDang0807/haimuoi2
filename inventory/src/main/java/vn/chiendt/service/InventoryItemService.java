package vn.chiendt.service;

import vn.chiendt.model.InventoryItem;

public interface InventoryItemService {
    
    /**
     * Initialize inventory for a new product
     * @param productId the product ID
     * @param initialQuantity the initial stock quantity
     * @return the created inventory item
     */
    InventoryItem initializeInventory(Long productId, Long initialQuantity);
    
    /**
     * Get inventory item by product ID
     * @param productId the product ID
     * @return the inventory item
     */
    InventoryItem getInventoryByProductId(Long productId);
    
    /**
     * Update available quantity
     * @param productId the product ID
     * @param quantity the quantity to add/subtract
     * @param isAddition true for addition, false for subtraction
     * @return the updated inventory item
     */
    InventoryItem updateAvailableQuantity(Long productId, Long quantity, boolean isAddition);
    
    /**
     * Reserve quantity for an order
     * @param productId the product ID
     * @param quantity the quantity to reserve
     * @return the updated inventory item
     */
    InventoryItem reserveQuantity(Long productId, Long quantity);
    
    /**
     * Release reserved quantity
     * @param productId the product ID
     * @param quantity the quantity to release
     * @return the updated inventory item
     */
    InventoryItem releaseReservedQuantity(Long productId, Long quantity);
}
