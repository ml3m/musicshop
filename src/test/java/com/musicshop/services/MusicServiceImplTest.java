package com.musicshop.services;

import com.musicshop.exceptions.InvalidItemException;
import com.musicshop.models.Album;
import com.musicshop.models.MusicItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MusicServiceImplTest {
    private InventoryServiceImpl inventoryService;
    private MusicServiceImpl musicService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl();
        musicService = new MusicServiceImpl(inventoryService);
    }

    @Test
    void testAddItem() throws InvalidItemException {
        MusicItem album = new Album("Back in Black", 15.0, "AC/DC", 1980);
        musicService.addItem(album);

        List<MusicItem> items = inventoryService.getItems();
        assertEquals(1, items.size());
        assertEquals("Back in Black", items.get(0).getName());
    }

    @Test
    void testRemoveItem() throws InvalidItemException {
        MusicItem album = new Album("Greatest Hits", 10.0, "Queen", 1981);
        musicService.addItem(album);

        musicService.removeItem("Greatest Hits");
        List<MusicItem> items = inventoryService.getItems();
        assertTrue(items.isEmpty());
    }

    @Test
    void testRemoveNonexistentItem() {
        assertThrows(InvalidItemException.class, () -> musicService.removeItem("Nonexistent"));
    }
}
