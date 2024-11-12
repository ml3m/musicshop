package com.musicshop.services.music;

import com.musicshop.models.music.MusicItem;
import com.musicshop.exceptions.InvalidItemException;
import com.musicshop.services.inventory.InventoryService;

public class MusicService implements MusicServiceInterface {
    private final InventoryService inventoryService;

    public MusicService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void addItem(MusicItem item) {
        if (item == null || item.getPrice() < 0) {
            throw new InvalidItemException("Invalid item details");
        }
        inventoryService.addItem(item);
    }

    public void removeItem(String itemName) {
        System.out.println("Attempting to remove item: " + itemName);

        //itemToRemove = inventoryService.findItemByName(itemName);

        inventoryService.getItems().remove(itemName);
        inventoryService.saveItemsInInventory();
        //System.out.println("Item '" + itemToRemove.getName() + "' has been removed from the inventory.");
    }
}