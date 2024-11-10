package com.musicshop.services.user;

import com.musicshop.models.user.User;
import com.musicshop.exceptions.AuthenticationException;
import com.musicshop.models.user.WorkLog;
import com.musicshop.services.storage.FileStorageService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static class WorkLogService {
        private final FileStorageService fileStorageService;
        private final Map<String, WorkLog> activeWorkLogs;
        private final List<WorkLog> workLogs;

        public WorkLogService(FileStorageService fileStorageService) {
            this.fileStorageService = fileStorageService;
            this.activeWorkLogs = new HashMap<>();
            this.workLogs = loadWorkLogs();
        }

        public WorkLog checkIn(String userId) {
            if (activeWorkLogs.containsKey(userId)) {
                throw new IllegalStateException("User already checked in");
            }
            WorkLog workLog = new WorkLog(userId);
            activeWorkLogs.put(userId, workLog);
            return workLog;
        }

        public WorkLog checkOut(String userId) {
            WorkLog workLog = activeWorkLogs.remove(userId);
            if (workLog == null) {
                throw new IllegalStateException("User not checked in");
            }
            workLog.checkOut();
            workLogs.add(workLog);
            saveWorkLogs();
            return workLog;
        }

        public Duration getTotalWorkTime(String userId) {
            return workLogs.stream()
                    .filter(log -> log.getUserId().equals(userId))
                    .map(WorkLog::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);
        }

        public Map<String, Duration> getAllWorkLogs() {
            Map<String, Duration> totalWorkTimeByUser = new HashMap<>();
            for (WorkLog log : workLogs) {
                totalWorkTimeByUser.merge(
                    log.getUserId(),
                    log.getDuration(),
                    Duration::plus
                );
            }
            return totalWorkTimeByUser;
        }

        private List<WorkLog> loadWorkLogs() {
            return fileStorageService.loadWorkLogs();
        }

        private void saveWorkLogs() {
            fileStorageService.saveWorkLogs(workLogs);
        }
    }
}
