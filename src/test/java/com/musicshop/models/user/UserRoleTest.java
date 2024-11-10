package com.musicshop.models.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {
    @Test
    void testUserRoleValues() {
        assertEquals(3, UserRole.values().length);
        assertTrue(containsRole(UserRole.values(), UserRole.ADMIN));
        assertTrue(containsRole(UserRole.values(), UserRole.SHOP_EMPLOYEE));
        assertTrue(containsRole(UserRole.values(), UserRole.ACCOUNTANT));
    }

    private boolean containsRole(UserRole[] roles, UserRole roleToFind) {
        for (UserRole role : roles) {
            if (role == roleToFind) return true;
        }
        return false;
    }
}
