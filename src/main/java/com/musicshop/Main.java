package com.musicshop;

import com.musicshop.services.analytics_dashboard.AnalyticsService;
import com.musicshop.services.inventory.InventoryService;
import com.musicshop.services.music.MusicService;
import com.musicshop.services.order.OrderService;
import com.musicshop.services.storage.FileStorageService;
import com.musicshop.services.user.AuthenticationService;
import com.musicshop.services.user.UserService;

public class Main {
    public static void main(String[] args) {
        // config read
        String startMode = Config.getStartMode();
        boolean viewInventory = Config.getShowTest();
        boolean viewOrders = Config.getViewOrders();
        boolean addInventoryItem = Config.getAddInventoryItem();
        boolean configAllow = Config.getConfigAllow();

        System.out.println("Application started in " + startMode + " mode.");

        // Init services
        FileStorageService fileStorageService = new FileStorageService();
        InventoryService inventoryService = new InventoryService(fileStorageService);
        MusicService musicService = new MusicService(inventoryService);
        OrderService orderService = new OrderService(fileStorageService);
        UserService userService = new UserService(fileStorageService);
        AuthenticationService authService = new AuthenticationService(userService);
        AuthenticationService.WorkLogService workLogService = new AuthenticationService.WorkLogService(fileStorageService);
        AnalyticsService analyticsService = new AnalyticsService(orderService, inventoryService);

        // Init MainMenu with all services
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

        if (configAllow) {
            if (viewInventory) {
                System.out.println("\n--- Inventory ---");
                mainMenu.viewItems();
            }
            if (viewOrders) {
                System.out.println("\n--- Orders ---");
                mainMenu.viewOrders();
            }
            if (addInventoryItem) {
                System.out.println("\n--- Add Inventory Item ---");
                mainMenu.addItem();
            }
        } else {
            System.out.println("Configuration changes are not allowed.");
        }

        // start app
        mainMenu.start();
    }
}
