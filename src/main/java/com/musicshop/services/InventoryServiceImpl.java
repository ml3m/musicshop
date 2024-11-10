package com.musicshop.services;

import com.musicshop.models.MusicItem;
import com.musicshop.models.SearchCriteria;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

    public List<MusicItem> searchItems(SearchCriteria criteria) {
        return getItems().stream()
            .filter(item -> matchesCriteria(item, criteria))
            .collect(Collectors.toList());
    }

    private boolean matchesCriteria(MusicItem item, SearchCriteria criteria) {
        return (criteria.getKeyword() == null || 
                item.getName().toLowerCase().contains(criteria.getKeyword().toLowerCase())) &&
               (criteria.getMinPrice() == null || item.getPrice() >= criteria.getMinPrice()) &&
               (criteria.getMaxPrice() == null || item.getPrice() <= criteria.getMaxPrice()) &&
               (criteria.getItemType() == null || 
                item.getClass().getSimpleName().equalsIgnoreCase(criteria.getItemType())) &&
               (criteria.getInStock() == null || 
                (criteria.getInStock() && item.getQuantity() > 0) ||
                (!criteria.getInStock() && item.getQuantity() == 0));
    }

    public void addItem(MusicItem newItem) {
        MusicItem existingItem = findItemByName(newItem.getName());
        if (existingItem != null) {
            existingItem.increaseQuantity(newItem.getQuantity());
            System.out.println("Increased quantity of " + newItem.getName() + " to " + existingItem.getQuantity());
        } else {
            inventory.add(newItem);
            System.out.println("Added new item: " + newItem.getName());
        }
        fileStorageService.saveItems(inventory); // Save inventory after modification
    }

    public void editItemQuantity(String itemName, int newQuantity) {
        MusicItem item = findItemByName(itemName);
        if (item != null) {
            if (newQuantity > 0) {
                item.setQuantity(newQuantity);
                System.out.println("Quantity of " + itemName + " updated to " + newQuantity);
                fileStorageService.saveItems(inventory); // Save the inventory after modification
            } else {
                System.out.println("Invalid quantity. It must be greater than zero.");
            }
        } else {
            System.out.println("Item not found in inventory: " + itemName);
        }
    }

    public void removeItem(String itemName) {
        inventory.removeIf(item -> item.getName().equalsIgnoreCase(itemName));
        fileStorageService.saveItems(inventory); // Save full inventory after removal
    }

    public void clearItems() {
        inventory.clear();
        fileStorageService.clearAllItems(); // Clear the JSON file
    }

    // New method to save inventory using FileStorageService
    public void saveItemsInInventory() {
        fileStorageService.saveItems(inventory);
    }
}
