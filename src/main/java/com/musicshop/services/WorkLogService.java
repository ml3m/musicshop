package com.musicshop.services;

import com.musicshop.models.WorkLog;
import java.util.*;
import java.time.Duration;

public class WorkLogService {
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
