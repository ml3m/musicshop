package com.musicshop;

import com.musicshop.models.Album;
import com.musicshop.models.Instrument;
import com.musicshop.models.MusicItem;
import com.musicshop.services.InventoryServiceImpl;
import com.musicshop.services.MusicServiceImpl;
import com.musicshop.exceptions.InvalidItemException;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private final MusicServiceImpl musicService;
    private final InventoryServiceImpl inventoryService;
    private final Scanner scanner;

    public MainMenu(MusicServiceImpl musicService, InventoryServiceImpl inventoryService) {
        this.musicService = musicService;
        this.inventoryService = inventoryService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            showMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1 -> addItem();
                case 2 -> removeItem();
                case 3 -> viewItems();
                case 0 -> {
                    System.out.println("Exiting program...");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void showMenu() {
        System.out.println("\n--- Music Shop Management System ---");
        System.out.println("1. Add Item to Inventory");
        System.out.println("2. Remove Item from Inventory");
        System.out.println("3. View Inventory");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    private int getUserChoice() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.next();  // Clear invalid input
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }

private void addItem() {
    System.out.println("Choose item type: 1. Instrument  2. Album");
    int type = getUserChoice();
    scanner.nextLine();  // Consume newline

    try {
        if (type == 1) {
            String type_selected_jsonID = "instrument";

            System.out.print("Enter Instrument name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Instrument price: ");
            double price = scanner.nextDouble();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter Instrument type (e.g., Guitar, Piano): ");
            String instrumentType = scanner.nextLine();

            // fix arguments (instrument)
            MusicItem instrument = new Instrument(name, price, type_selected_jsonID);
            musicService.addItem(instrument);

            System.out.println("Instrument added to inventory.");

        } else if (type == 2) {
            String type_selected_jsonID = "album";

            System.out.print("Enter Album name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Album price: ");
            double price = scanner.nextDouble();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter Album artist: ");
            String artist = scanner.nextLine();
            System.out.print("Enter Album release year: ");
            int releaseYear = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter Album type (e.g., Vinyl, CD): ");
            String albumType = scanner.nextLine();

            // fix arguments (album)
            MusicItem album = new Album(name, price, artist, releaseYear, type_selected_jsonID);
            musicService.addItem(album);


            System.out.println("Album added to inventory.");
        } else {
            System.out.println("Invalid item type selected.");
        }
    } catch (InvalidItemException | InputMismatchException e) {
        System.out.println("Failed to add item: " + e.getMessage());
        scanner.nextLine();  // Clear invalid input if any
    }
}

    private void removeItem() {
        System.out.print("Enter name of the item to remove: ");
        scanner.nextLine();  // Consume newline
        String itemName = scanner.nextLine();

        try {
            musicService.removeItem(itemName);
            System.out.println("Item removed from inventory.");
        } catch (InvalidItemException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewItems() {
        List<MusicItem> items = inventoryService.getItems();
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
        } else {
            System.out.println("\n--- Inventory List ---");
            items.forEach(System.out::println);
        }
    }
}
