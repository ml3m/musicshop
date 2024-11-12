package com.musicshop.services.inventory;

import com.musicshop.models.music.MusicItem;
import java.util.List;

public interface InventoryServiceInterface {
    List<MusicItem> getItems();
    MusicItem findItemByName(String name);
}