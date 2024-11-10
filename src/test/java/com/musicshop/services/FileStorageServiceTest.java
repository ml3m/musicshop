package com.musicshop.services;

import com.musicshop.models.music.MusicItem;
import com.musicshop.models.music.Album;
import com.musicshop.services.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import java.util.List;      // Import List
import java.util.ArrayList; // Import ArrayList
import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @Test
    void testLoadItems() {
        FileStorageService fileStorageService = new FileStorageService();
        // Assuming the file exists and contains items
        List<MusicItem> items = fileStorageService.loadItems();
        assertNotNull(items, "Loaded items should not be null");
        assertTrue(items.size() > 0, "Loaded items list should contain items");
    }

    @Test
    void testSaveItems() {
        FileStorageService fileStorageService = new FileStorageService();
        List<MusicItem> items = new ArrayList<>();
        items.add(new Album("Test Album", 20.0, "Test Artist", 2020, "album"));
        
        fileStorageService.saveItems(items);
        
        List<MusicItem> loadedItems = fileStorageService.loadItems();
        assertEquals(1, loadedItems.size(), "The saved item should be retrievable from the file");
    }
}
