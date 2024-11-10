package com.musicshop.models.music;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MusicItemTest {
    // Create a concrete implementation for testing
    private static class TestMusicItem extends MusicItem {
        public TestMusicItem(String name, double price, int quantity) {
            super(name, price, quantity);
        }
        
        @Override
        public String getType() {
            return "test";
        }
    }

    @Test
    void testMusicItemConstructor() {
        MusicItem item = new TestMusicItem("Test Item", 99.99, 5);
        assertEquals("Test Item", item.getName());
        assertEquals(99.99, item.getPrice());
        assertEquals(5, item.getQuantity());
    }

    @Test
    void testSettersAndGetters() {
        MusicItem item = new TestMusicItem("Test Item", 99.99, 5);
        
        item.setName("New Name");
        assertEquals("New Name", item.getName());
        
        item.setPrice(149.99);
        assertEquals(149.99, item.getPrice());
        
        item.setQuantity(10);
        assertEquals(10, item.getQuantity());
        
        item.setBarcode("12345");
        assertEquals("12345", item.getBarcode());
    }

    @Test
    void testIncreaseQuantity() {
        MusicItem item = new TestMusicItem("Test Item", 99.99, 5);
        item.increaseQuantity(3);
        assertEquals(8, item.getQuantity());
    }

    @Test
    void testToString() {
        MusicItem item = new TestMusicItem("Test Item", 99.99, 5);
        assertEquals("Name: Test Item, Price: $99.99, Quantity: 5", item.toString());
    }
}
