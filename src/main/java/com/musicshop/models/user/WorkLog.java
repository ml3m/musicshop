package com.musicshop.models.user;

import java.time.LocalDateTime;
import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class WorkLog {
    private String userId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    @JsonIgnore // json ignore basically
    private transient Duration duration;

    public WorkLog(){}

    public WorkLog(String userId) {
        this.userId = userId;
        this.checkInTime = LocalDateTime.now();
    }

    public void checkOut() {
        this.checkOutTime = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public Duration getDuration() {
        if (checkOutTime == null) {
            return Duration.between(checkInTime, LocalDateTime.now());
        }
        return Duration.between(checkInTime, checkOutTime);
    }

    // Add getters for serialization if needed
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    // Add setters for deserialization if needed
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
