package com.musicshop.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.musicshop.models.MusicItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileStorageService {
    private static final String FILE_PATH = "inventory.json";
    private final ObjectMapper objectMapper;

    public FileStorageService() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
    }

    // Load items from JSON file
    public List<MusicItem> loadItems() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();  // Return empty list if file doesn't exist
            }
            // Deserialize JSON into List<MusicItem>, correctly handling subtypes based on 'type' field
            return objectMapper.readValue(file, new TypeReference<List<MusicItem>>() {});
        } catch (IOException e) {
            System.out.println("Error loading items from JSON: " + e.getMessage());
            return new ArrayList<>();  // Return empty list in case of error
        }
    }

    // Save items to JSON file
    public void saveItems(List<MusicItem> items) {
        try {
            objectMapper.writeValue(new File(FILE_PATH), items);
            System.out.println("Inventory saved to JSON file.");
        } catch (IOException e) {
            System.out.println("Error saving items to JSON: " + e.getMessage());
        }
    }

    // Append a single item to the existing JSON file contents
    public void appendItem(MusicItem newItem) {
        List<MusicItem> currentItems = loadItems(); // Load current items
        currentItems.add(newItem); // Add the new item
        saveItems(currentItems); // Save the updated list
    }

    // Clear all items in the JSON file
    public void clearAllItems() {
        saveItems(new ArrayList<>()); // Save an empty list to clear the file
    }
}
