package com.musicshop.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private Order order;
    private Customer customer;
    private MusicItem albumItem;
    private MusicItem instrumentItem;

    @BeforeEach
    void setUp() {
        customer = new Customer("C001", "John Doe");
        order = new Order(customer);

        // Creating instances of concrete subclasses of MusicItem

        //public Album(String name, double price, String artist, int year, String type, int quantity, String barcode) {
        albumItem = new Album("Test Album", 99.99, "artist name", 2023, "album", 10, "barcode-test"); // Assuming Album has a constructor with name, price, quantity, and year
                                                                                                      //
        //public Instrument(String name, double price, String type, int quantity, String barcode) {
        instrumentItem = new Instrument("Test Instrument", 199.99, "instrument", 10, "barcode-test"); // Assuming Instrument has a constructor with name, price, and quantity
    }

    @Test
    void testOrderConstructor() {
        assertNotNull(order);
        assertEquals(customer, order.getCustomer());
        assertNotNull(order.getOrderDate());
        assertTrue(order.getCartItems().isEmpty());
    }

    @Test
    void testAddItem() {
        order.addItem(albumItem);
        assertEquals(1, order.getCartItems().size()); // Using getCartItems()
        assertTrue(order.getCartItems().contains(albumItem));
    }

    @Test
    void testCalculateTotal() {
        order.addItem(albumItem);
        order.addItem(instrumentItem);
        double expectedTotal = albumItem.getPrice() + instrumentItem.getPrice();
        assertEquals(expectedTotal, order.getTotalAmount());
    }
}
