package com.musicshop.services;

import com.musicshop.models.MusicItem;
import java.util.List;
import java.util.ArrayList;

public class InventoryServiceImpl implements InventoryService {
    private final List<MusicItem> inventory;
    private final FileStorageService fileStorageService;

    public InventoryServiceImpl(FileStorageService fileStorageService) {
        this.inventory = new ArrayList<>();
        this.fileStorageService = fileStorageService;

        // Load items from JSON into memory on initialization
        List<MusicItem> loadedItems = fileStorageService.loadItems();
        inventory.addAll(loadedItems); // Populate in-memory inventory
    }

    @Override
    public List<MusicItem> getItems() {
        return new ArrayList<>(inventory); // Return a copy to avoid external modifications
    }

    @Override
    public MusicItem findItemByName(String name) {
        return inventory.stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void addItem(MusicItem item) {
        inventory.add(item); 
        fileStorageService.appendItem(item); // Append single item to file
    }

    public void removeItem(String itemName) {
        inventory.removeIf(item -> item.getName().equalsIgnoreCase(itemName));
        fileStorageService.saveItems(inventory); // Save full inventory after removal
    }

    public void clearItems() {
        inventory.clear();
        fileStorageService.clearAllItems(); // Clear the JSON file
    }
}
