package com.musicshop.services.music;

import com.musicshop.models.music.MusicItem;

public interface MusicService {
    void addItem(MusicItem item);
    void removeItem(String itemName);
}
