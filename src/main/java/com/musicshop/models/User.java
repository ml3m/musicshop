package com.musicshop.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private String id;  // Randomly generated string ID
    private String username;
    private String password;
    private UserRole role;
    private boolean active;
    private LocalDateTime lastLogin;

    // Default constructor with random ID generation
    public User() {
        this.id = generateRandomId();
        this.active = true; // Default to active when created
    }

    // Constructor with user-defined fields and random ID generation
    public User(String username, String password, UserRole role) {
        this.id = generateRandomId();
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = true;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Setter for id if manual setting is required (optional)
    public void setId(String id) {
        this.id = id;
    }

    // Generate a random unique identifier using UUID
    private String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    // Other getters and setters remain the same
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
