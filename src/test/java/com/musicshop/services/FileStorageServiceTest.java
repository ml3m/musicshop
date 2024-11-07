package com.musicshop.services;

import com.musicshop.models.Album;
import com.musicshop.models.Instrument;
import com.musicshop.models.MusicItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileStorageServiceTest {
    private FileStorageService fileStorageService;
    private InventoryServiceImpl inventoryService;
    private static final String TEST_FILE_PATH = "test_inventory.json";

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        inventoryService = new InventoryServiceImpl();
    }

    @Test
    void testSaveAndLoadItems() {
        List<MusicItem> items = new ArrayList<>();
        items.add(new Album("Test Album", 10.0, "Test Artist", 2021));
        items.add(new Instrument("Violin", 120.0, "String"));

        // Save to file
        fileStorageService.saveItems(items);

        // Load from file
        inventoryService.clearItems();
        fileStorageService.loadItems(inventoryService);
        List<MusicItem> loadedItems = inventoryService.getItems();

        assertEquals(2, loadedItems.size());
        assertEquals("Test Album", loadedItems.get(0).getName());
        assertEquals("Violin", loadedItems.get(1).getName());

        // Clean up test file
        new File(TEST_FILE_PATH).delete();
    }
}
