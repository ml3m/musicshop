package com.musicshop.services.user;

import com.musicshop.models.user.User;
import com.musicshop.models.user.UserRole;
import com.musicshop.services.storage.FileStorageService;

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

    public void createUser(String username, String password, UserRole role) {
        if (findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User(username, password, role);
        users.add(user);
        saveUsers();
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

    public List<User> getAllUsers() { return new ArrayList<>(users); }

    public void updateUser(User user) {
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        saveUsers();
    }

    private List<User> loadUsers() { return fileStorageService.loadUsers(); }
    private void saveUsers() { fileStorageService.saveUsers(users); }
}
