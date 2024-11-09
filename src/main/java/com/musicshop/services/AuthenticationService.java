package com.musicshop.services;

import com.musicshop.models.User;
import com.musicshop.exceptions.AuthenticationException;
import com.musicshop.models.UserRole;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthenticationService {
    private final UserService userService;
    private User currentUser;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    public User login(String username, String password) throws AuthenticationException {
        Optional<User> user = userService.findByUsername(username);
        
        if (user.isPresent() && user.get().getPassword().equals(password)) {  // In real app, use password hashing
            if (!user.get().isActive()) {
                throw new AuthenticationException("Account is disabled");
            }
            user.get().setLastLogin(LocalDateTime.now());
            userService.updateUser(user.get());
            currentUser = user.get();
            return currentUser;
        }
        throw new AuthenticationException("Invalid username or password");
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public boolean hasRole(UserRole... roles) {
        if (!isAuthenticated()) return false;
        for (UserRole role : roles) {
            if (currentUser.getRole() == role) return true;
        }
        return false;
    }
}
