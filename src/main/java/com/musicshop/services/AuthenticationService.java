package com.musicshop.services;

import com.musicshop.models.User;
import com.musicshop.exceptions.AuthenticationException;
import java.time.LocalDateTime;
import java.util.Optional;

public class AuthenticationService {
    private final UserService userService;
    private User currentUser;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    public User login(String username, String password) throws AuthenticationException {
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isPresent() && userOpt.get().checkPassword(password)) {  // Verifies hashed password
            User user = userOpt.get();
            if (!user.isActive()) {
                throw new AuthenticationException("Account is disabled");
            }
            user.setLastLogin(LocalDateTime.now());
            userService.updateUser(user);
            currentUser = user;
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
}
