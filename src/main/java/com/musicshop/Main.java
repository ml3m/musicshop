package com.musicshop;

import com.musicshop.services.*;

public class Main {
    public static void main(String[] args){
        // Initialize services
        FileStorageService fileStorageService = new FileStorageService();
        
        // Core services
        InventoryServiceImpl inventoryService = new InventoryServiceImpl(fileStorageService);
        MusicServiceImpl musicService = new MusicServiceImpl(inventoryService);
        OrderServiceImpl orderService = new OrderServiceImpl();
        
        // User management services
        UserService userService = new UserService(fileStorageService);
        AuthenticationService authService = new AuthenticationService(userService);
        WorkLogService workLogService = new WorkLogService(fileStorageService);
        
        // Analytics service
        AnalyticsService analyticsService = new AnalyticsService(orderService, inventoryService);

        // Initialize main menu with all services
        MainMenu mainMenu = new MainMenu(
            musicService,
            inventoryService,
            orderService,
            authService,
            userService,
            workLogService,
            analyticsService
        );

        // Start the application
        mainMenu.start();
    }
}
