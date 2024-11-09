package com.musicshop.services;

import com.musicshop.models.User;
import com.musicshop.models.UserRole;
import java.util.*;

public class UserService {
    private final FileStorageService fileStorageService;
    private final List<User> users;

    public UserService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.users = loadUsers();
        
        // Create default admin if no users exist
        if (users.isEmpty()) {
            createUser("admin", "admin123", UserRole.ADMIN);
        }
    }

    public User createUser(String username, String password, UserRole role) {
        if (findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User(username, password, role);
        users.add(user);
        saveUsers();
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public Optional<User> findById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public void updateUser(User user) {
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        saveUsers();
    }

    public void deleteUser(String userId) {
        users.removeIf(u -> u.getId().equals(userId));
        saveUsers();
    }

    private List<User> loadUsers() {
        return fileStorageService.loadUsers();
    }

    private void saveUsers() {
        fileStorageService.saveUsers(users);
    }
}
