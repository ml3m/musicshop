package com.musicshop.services;

import com.musicshop.models.Album;
import com.musicshop.models.Instrument;
import com.musicshop.models.MusicItem;
import com.musicshop.exceptions.InvalidItemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MusicServiceImplTest {

    private MusicServiceImpl musicService;
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl(new FileStorageService());
        musicService = new MusicServiceImpl(inventoryService);
    }

    @Test
    void testAddItem() {
        MusicItem album = new Album("Abbey Road", 30.0, "The Beatles", 1969, "album");
        musicService.addItem(album);
        assertTrue(inventoryService.getItems().contains(album), "Item should be added to the inventory");
    }

    @Test
    void testRemoveItem() {
        MusicItem instrument = new Instrument("Piano", 500.0, "Grand");
        musicService.addItem(instrument);
        musicService.removeItem("Piano");
        assertFalse(inventoryService.getItems().contains(instrument), "Item should be removed from the inventory");
    }

    @Test
    void testAddItemWithInvalidPrice() {
        MusicItem instrument = new Instrument("Drum", -50.0, "Percussion");
        assertThrows(InvalidItemException.class, () -> musicService.addItem(instrument), "Adding item with invalid price should throw exception");
    }

    @Test
    void testRemoveNonExistentItem() {
        assertThrows(InvalidItemException.class, () -> musicService.removeItem("NonExistentItem"), "Removing a non-existent item should throw exception");
    }
}
