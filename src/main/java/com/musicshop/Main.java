package com.musicshop;

import com.musicshop.services.FileStorageService;
import com.musicshop.services.InventoryServiceImpl;
import com.musicshop.services.MusicServiceImpl;

public class Main {
    public static void main(String[] args) {
        // Instantiate the file storage service
        FileStorageService fileStorageService = new FileStorageService();

        // Instantiate the inventory service with file storage for automatic load/save
        InventoryServiceImpl inventoryService = new InventoryServiceImpl(fileStorageService);

        // Instantiate the music service, passing the inventory service
        MusicServiceImpl musicService = new MusicServiceImpl(inventoryService);

        // Pass all services to MainMenu
        MainMenu mainMenu = new MainMenu(musicService, inventoryService);

        // Start the application
        mainMenu.start();
    }
}
