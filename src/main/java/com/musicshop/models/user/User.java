package com.musicshop.models.user;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCrypt;

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
        setPassword(password);
        this.role = role;
        this.active = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    private String generateRandomId() { return UUID.randomUUID().toString(); }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }

    // Prevents double hashing by checking if the password is already hashed
    public void setPassword(String password) {
        if (isAlreadyHashed(password)) {
            this.password = password;  // Assume it's already hashed
        } else {
            this.password = hashPassword(password);  // Hash if not already
        }
    }

    // Check if a password is already hashed (approximation based on BCrypt structure)
    private boolean isAlreadyHashed(String password) {
        return password != null && password.startsWith("$2a$") && password.length() == 60;
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());  // Hash with BCrypt
    }

    public boolean checkPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.password);  // Validate hashed password
    }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
}