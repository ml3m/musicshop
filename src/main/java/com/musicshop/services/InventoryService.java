package com.musicshop.services;

import com.musicshop.models.MusicItem;
import java.util.List;

public interface InventoryService {
    List<MusicItem> getItems();
    MusicItem findItemByName(String name);
}
