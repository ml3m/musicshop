package com.musicshop.models.user;

import java.time.LocalDateTime;
import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class WorkLog {
    private String userId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    @JsonIgnore
    private transient Duration duration;

    // json requires
    public WorkLog(){}

    public WorkLog(String userId) {
        this.userId = userId;
        this.checkInTime = LocalDateTime.now();
    }

    public Duration getDuration() {
        if (checkOutTime == null) {
            return Duration.between(checkInTime, LocalDateTime.now());
        }
        return Duration.between(checkInTime, checkOutTime);
    }

    public void checkOut() { this.checkOutTime = LocalDateTime.now();}
    public String getUserId() { return userId; }
    public LocalDateTime getCheckInTime() { return checkInTime; }
    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }
}
