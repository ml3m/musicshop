package com.musicshop.services;

import com.musicshop.models.Album;
import com.musicshop.models.Instrument;
import com.musicshop.models.MusicItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceImplTest {
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl();
    }

    @Test
    void testAddItem() {
        MusicItem guitar = new Instrument("Guitar", 150.0, "String");
        inventoryService.addItem(guitar);

        List<MusicItem> items = inventoryService.getItems();
        assertEquals(1, items.size());
        assertEquals("Guitar", items.get(0).getName());
    }

    @Test
    void testRemoveItem() {
        MusicItem piano = new Instrument("Piano", 1000.0, "Percussion");
        inventoryService.addItem(piano);
        inventoryService.removeItem("Piano");

        List<MusicItem> items = inventoryService.getItems();
        assertTrue(items.isEmpty());
    }

    @Test
    void testGetItems() {
        inventoryService.addItem(new Album("Thriller", 15.0, "Michael Jackson", 1982));
        inventoryService.addItem(new Instrument("Drums", 200.0, "Percussion"));

        List<MusicItem> items = inventoryService.getItems();
        assertEquals(2, items.size());
    }
}
