package com.musicshop;

import com.musicshop.services.analytics_dashboard.AnalyticsService;
import com.musicshop.services.inventory.InventoryServiceImpl;
import com.musicshop.services.music.MusicServiceImpl;
import com.musicshop.services.order.OrderServiceImpl;
import com.musicshop.services.storage.FileStorageService;
import com.musicshop.services.user.AuthenticationService;
import com.musicshop.services.user.UserService;

public class Main {
    public static void main(String[] args) {

        // Initialize services
        FileStorageService fileStorageService = new FileStorageService();
        InventoryServiceImpl inventoryService = new InventoryServiceImpl(fileStorageService);
        MusicServiceImpl musicService = new MusicServiceImpl(inventoryService);
        OrderServiceImpl orderService = new OrderServiceImpl();
        UserService userService = new UserService(fileStorageService);
        AuthenticationService authService = new AuthenticationService(userService);
        AuthenticationService.WorkLogService workLogService = new AuthenticationService.WorkLogService(fileStorageService);
        AnalyticsService analyticsService = new AnalyticsService(orderService, inventoryService);
        

        // Initialize main menu with all services
        MainMenu mainMenu = new MainMenu(
            musicService,
            inventoryService,
            orderService,
            authService,
            userService,
            workLogService,
            analyticsService,
            fileStorageService
        );
        
        // Start the application
        mainMenu.start();
    }
}
