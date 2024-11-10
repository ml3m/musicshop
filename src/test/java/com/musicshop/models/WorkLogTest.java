package com.musicshop.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class WorkLogTest {

    @Test
    void testWorkLogConstructor() {
        String userId = "U001";
        WorkLog workLog = new WorkLog(userId);

        assertEquals(userId, workLog.getUserId());
        assertNotNull(workLog.getCheckInTime()); // Check that checkInTime is set to current time
        assertNull(workLog.getCheckOutTime()); // Since the check-out hasn't happened yet
    }

    @Test
    void testCheckOut() {
        WorkLog workLog = new WorkLog("U001");
        LocalDateTime initialCheckOut = workLog.getCheckOutTime(); // Should be null initially
        assertNull(initialCheckOut); // Confirm check-out is not set initially

        workLog.checkOut(); // This sets the check-out time
        assertNotNull(workLog.getCheckOutTime()); // Check that check-out is now set
    }

    @Test
    void testCalculateDuration() {
        WorkLog workLog = new WorkLog("U001");
        LocalDateTime checkInTime = workLog.getCheckInTime();
        
        // Simulate check-out after a fixed duration
        LocalDateTime checkOutTime = checkInTime.plusHours(8);
        workLog.setCheckOutTime(checkOutTime);

        // Check that the calculated duration matches the expected value
        assertEquals(8, workLog.getDuration().toHours());
    }
}
