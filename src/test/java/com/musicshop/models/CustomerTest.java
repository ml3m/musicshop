package com.musicshop.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {
    @Test
    void testCustomerConstructor() {
        Customer customer = new Customer("C001", "John Doe");
        assertNotNull(customer);
        assertEquals("C001", customer.getId());
        assertEquals("John Doe", customer.getName());
    }

    @Test
    void testCustomerToString() {
        Customer customer = new Customer("C001", "John Doe");
        assertEquals("ID: C001, Name: John Doe", customer.toString());
    }

    @Test
    void testEmptyConstructor() {
        Customer customer = new Customer();
        assertNotNull(customer);
        assertNull(customer.getId());
        assertNull(customer.getName());
    }
}
