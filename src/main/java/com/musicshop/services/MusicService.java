package com.musicshop.services;

import com.musicshop.models.MusicItem;

public interface MusicService {
    void addItem(MusicItem item);
    void removeItem(String itemName);
}
