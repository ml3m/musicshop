package com.musicshop.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {

    @Test
    void testAlbumCreation() {
        // Create an album object using the parameterized constructor
        MusicItem album = new Album("Thriller", 15.0, "Michael Jackson", 1982, "album");

        // Cast album back to Album to access specific methods
        Album castedAlbum = (Album) album;

        // Verify the album's properties
        assertEquals("Thriller", castedAlbum.getName(), "Album name should be 'Thriller'");
        assertEquals(15.0, castedAlbum.getPrice(), 0.001, "Album price should be 15.0");
        assertEquals("Michael Jackson", castedAlbum.getArtist(), "Album artist should be 'Michael Jackson'");
        assertEquals(1982, castedAlbum.getYear(), "Album year should be 1982");
        assertEquals("album", castedAlbum.getType(), "Album type should be 'album'");
    }

}
