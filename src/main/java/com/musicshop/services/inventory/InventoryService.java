package com.musicshop.services.inventory;

import com.musicshop.models.music.MusicItem;
import java.util.List;

public interface InventoryService {
    List<MusicItem> getItems();
    MusicItem findItemByName(String name);
}
