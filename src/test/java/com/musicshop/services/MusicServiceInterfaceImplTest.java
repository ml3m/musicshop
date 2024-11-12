package com.musicshop.services;

import com.musicshop.models.music.Album;
import com.musicshop.models.music.Instrument;
import com.musicshop.models.music.MusicItem;
import com.musicshop.exceptions.InvalidItemException;
import com.musicshop.services.inventory.InventoryService;
import com.musicshop.services.music.MusicService;
import com.musicshop.services.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MusicServiceInterfaceImplTest {

    private MusicService musicService;
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(new FileStorageService());
        musicService = new MusicService(inventoryService);
    }

    @Test
    void testAddItem() {
        MusicItem album = new Album("Abbey Road", 30.0, "The Beatles", 1969, "album");
        musicService.addItem(album);
        assertTrue(inventoryService.getItems().contains(album), "Item should be added to the inventory");
    }

    @Test
    void testAddItemWithInvalidPrice() {
        MusicItem instrument = new Instrument("Drum", -50.0, "Percussion");
        assertThrows(InvalidItemException.class, () -> musicService.addItem(instrument), "Adding item with invalid price should throw exception");
    }

}
