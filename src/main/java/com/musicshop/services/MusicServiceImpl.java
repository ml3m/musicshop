package com.musicshop.services;

import com.musicshop.models.MusicItem;
import com.musicshop.exceptions.InvalidItemException;

public class MusicServiceImpl implements MusicService {
    private final InventoryServiceImpl inventoryService;

    public MusicServiceImpl(InventoryServiceImpl inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void addItem(MusicItem item) {
        if (item == null || item.getPrice() < 0) {
            throw new InvalidItemException("Invalid item details");
        }
        inventoryService.addItem(item);
    }

    @Override
    public void removeItem(String itemName) {
        if (inventoryService.findItemByName(itemName) == null) {
            throw new InvalidItemException("Item not found in inventory");
        }
        inventoryService.removeItem(itemName);
    }
}

