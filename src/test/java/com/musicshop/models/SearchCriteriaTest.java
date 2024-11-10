package com.musicshop.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SearchCriteriaTest {
    @Test
    void testSearchCriteriaConstructor() {
        SearchCriteria criteria = new SearchCriteria("keyword", 10.0, 100.0, "Album", true);
        
        assertEquals("keyword", criteria.getKeyword());
        assertEquals(10.0, criteria.getMinPrice());
        assertEquals(100.0, criteria.getMaxPrice());
        assertEquals("Album", criteria.getItemType());
        assertTrue(criteria.getInStock());
    }

    @Test
    void testNullValues() {
        SearchCriteria criteria = new SearchCriteria(null, null, null, null, null);
        
        assertNull(criteria.getKeyword());
        assertNull(criteria.getMinPrice());
        assertNull(criteria.getMaxPrice());
        assertNull(criteria.getItemType());
        assertNull(criteria.getInStock());
    }
}
