package com.musicshop.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {

    @Test
    void testAlbumCreation() {
        Album album = new Album("Thriller", 15.0, "Michael Jackson", 1982);
        assertEquals("Thriller", album.getName());
        assertEquals(15.0, album.getPrice());
        assertEquals("Michael Jackson", album.getArtist());
        assertEquals(1982, album.getYear());
    }

    @Test
    void testAlbumToString() {
        Album album = new Album("Abbey Road", 20.0, "The Beatles", 1969);
        String expected = "Name: Abbey Road, Price: $20.0, Artist: The Beatles, Year: 1969";
        assertEquals(expected, album.toString());
    }
}
