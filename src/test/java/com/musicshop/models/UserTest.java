package com.musicshop.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void testUserConstructor() {
        User user = new User("testuser", "password", UserRole.ADMIN); // No ID argument
        
        // Since ID is generated randomly, you should not test for a specific ID here.
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(UserRole.ADMIN, user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User("testuser", "password", UserRole.ADMIN);
        
        user.setUsername("newuser");
        user.setPassword("newpassword");
        user.setRole(UserRole.SHOP_EMPLOYEE);
        user.setActive(false);

        assertEquals("newuser", user.getUsername());
        assertEquals("newpassword", user.getPassword());
        assertEquals(UserRole.SHOP_EMPLOYEE, user.getRole());
        assertFalse(user.isActive());
    }
}
