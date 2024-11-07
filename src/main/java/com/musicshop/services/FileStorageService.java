// File: com/musicshop/services/FileStorageService.java
package com.musicshop.services;

import com.musicshop.models.Order;
import com.musicshop.models.MusicItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileStorageService {
    private static final String INVENTORY_FILE_PATH = "inventory.json";
    private static final String ORDERS_FILE_PATH = "orders.json";
    private final ObjectMapper objectMapper;

    public FileStorageService() {
        this.objectMapper = new ObjectMapper();
    }

    // Load items from the inventory JSON file
    public List<MusicItem> loadItems() {
        try {
            File file = new File(INVENTORY_FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<MusicItem>>() {});
        } catch (IOException e) {
            System.out.println("Error loading items from JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Save items to the inventory JSON file
    public void saveItems(List<MusicItem> items) {
        try {
            objectMapper.writeValue(new File(INVENTORY_FILE_PATH), items);
            System.out.println("Inventory saved to JSON file.");
        } catch (IOException e) {
            System.out.println("Error saving items to JSON: " + e.getMessage());
        }
    }

    // Append a single item to the inventory JSON file
    public void appendItem(MusicItem item) {
        try {
            // Load current inventory
            List<MusicItem> items = loadItems();
            // Add new item
            items.add(item);
            // Save updated inventory
            saveItems(items);
            System.out.println("Item appended to inventory.");
        } catch (Exception e) {
            System.out.println("Error appending item to inventory: " + e.getMessage());
        }
    }

    // Clear all items in the inventory JSON file
    public void clearAllItems() {
        try {
            saveItems(new ArrayList<>()); // Save an empty list to clear the file
            System.out.println("All items cleared from inventory.");
        } catch (Exception e) {
            System.out.println("Error clearing inventory: " + e.getMessage());
        }
    }

    // Load orders from orders JSON file
    public List<Order> loadOrders() {
        try {
            File file = new File(ORDERS_FILE_PATH);
            if (file.exists()) {
                return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));
            }
        } catch (IOException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public void saveOrders(List<Order> orders) {
        try {
            objectMapper.writeValue(new File(ORDERS_FILE_PATH), orders);
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }
}
