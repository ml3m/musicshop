package com.musicshop.services.inventory;

import com.musicshop.models.music.MusicItem;
import com.musicshop.models.music.SearchCriteria;
import com.musicshop.services.storage.FileStorageService;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class InventoryService implements InventoryServiceInterface {
    private final List<MusicItem> inventory;
    private final FileStorageService fileStorageService;

    public InventoryService(FileStorageService fileStorageService) {
        this.inventory = new ArrayList<>();
        this.fileStorageService = fileStorageService;

        // Load items from JSON into memory on initialization
        List<MusicItem> loadedItems = fileStorageService.loadItems();
        inventory.addAll(loadedItems);
    }

    @Override
    public List<MusicItem> getItems() {
        return new ArrayList<>(inventory);
    }

    public List<MusicItem> getInventory(){
        return inventory;
    }

    @Override
    public MusicItem findItemByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null; // Handle null or empty name
        }

        String trimmedName = name.trim().toLowerCase(); // Convert the name to lower case once for comparison
        for (MusicItem item : inventory) {
            // for debuging
            //System.out.println("item in inventory name: " + item.getName());
            if (item.getName().toLowerCase().equals(trimmedName)) {
                return item; // Return the first match found
            }
        }

        return null; // No match found
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

    // New method to save inventory using FileStorageService
    // used to save edited Item
    public void saveItemsInInventory() {
        fileStorageService.saveItems(inventory);
    }
}