package com.musicshop;

import com.musicshop.models.Album;
import com.musicshop.models.Instrument;
import com.musicshop.models.MusicItem;
import com.musicshop.models.Order;
import com.musicshop.models.Customer;
import com.musicshop.services.InventoryServiceImpl;
import com.musicshop.services.MusicServiceImpl;
import com.musicshop.services.OrderServiceInterface;
import com.musicshop.exceptions.InvalidItemException;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private final MusicServiceImpl musicService;
    private final InventoryServiceImpl inventoryService;
    private final OrderServiceInterface orderService;  // Use OrderServiceInterface
    private final Scanner scanner;

    public MainMenu(MusicServiceImpl musicService, InventoryServiceImpl inventoryService, OrderServiceInterface orderService) {
        this.musicService = musicService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
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
                case 4 -> createOrder();
                case 5 -> viewOrders();
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
        System.out.println("4. Create Order");
        System.out.println("5. View Orders");
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

    private void createOrder() {
        System.out.print("Enter customer ID: ");
        scanner.nextLine();  // Consume newline
        String customerId = scanner.nextLine();
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();

        Customer customer = new Customer(customerId, customerName);
        Order order = new Order(customer);

        boolean addingItems = true;
        while (addingItems) {
            System.out.println("Choose product to add to cart:");
            List<MusicItem> items = inventoryService.getItems();
            for (int i = 0; i < items.size(); i++) {
                System.out.println((i + 1) + ". " + items.get(i).getName() + " - $" + items.get(i).getPrice());
            }
            System.out.print("Select product number (or 0 to finish): ");
            int productChoice = getUserChoice();
            if (productChoice == 0) {
                addingItems = false;
            } else if (productChoice > 0 && productChoice <= items.size()) {
                MusicItem item = items.get(productChoice - 1);
                order.addItem(item);
                System.out.println(item.getName() + " added to cart.");
            } else {
                System.out.println("Invalid selection.");
            }
        }

        orderService.processOrder(order);  // Process the order
        orderService.saveOrders(orderService.getAllOrders());  // Save orders after processing
        System.out.println("Order processed and saved successfully.");
    }

    private void viewOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
        } else {
            System.out.println("\n--- Order List ---");
            orders.forEach(System.out::println);
        }
    }
}
