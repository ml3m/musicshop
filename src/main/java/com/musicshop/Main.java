package com.musicshop;

import com.musicshop.services.FileStorageService;
import com.musicshop.services.InventoryServiceImpl;
import com.musicshop.services.MusicServiceImpl;
import com.musicshop.services.OrderServiceInterface;
import com.musicshop.services.OrderServiceImpl;

public class Main {
    public static void main(String[] args) {
        // Instantiate the file storage service
        FileStorageService fileStorageService = new FileStorageService();

        // Instantiate the inventory service with file storage for automatic load/save
        InventoryServiceImpl inventoryService = new InventoryServiceImpl(fileStorageService);

        // Instantiate the music service, passing the inventory service
        MusicServiceImpl musicService = new MusicServiceImpl(inventoryService);

        // Instantiate the order service implementation
        OrderServiceImpl orderService = new OrderServiceImpl();

        // Pass all services to MainMenu
        MainMenu mainMenu = new MainMenu(musicService, inventoryService, orderService);

        // Start the application
        mainMenu.start();
    }
}
