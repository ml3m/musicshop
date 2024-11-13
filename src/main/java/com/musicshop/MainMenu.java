package com.musicshop;

import com.musicshop.models.music.Album;
import com.musicshop.models.music.Instrument;
import com.musicshop.models.music.MusicItem;
import com.musicshop.models.music.SearchCriteria;
import com.musicshop.models.sales.Order;
import com.musicshop.models.sales.SalesReport;
import com.musicshop.models.user.Customer;
import com.musicshop.models.user.User;
import com.musicshop.models.user.UserRole;
import com.musicshop.exceptions.*;
import com.musicshop.models.user.WorkLog;
import com.musicshop.services.analytics_dashboard.AnalyticsService;
import com.musicshop.services.analytics_dashboard.ReportExportService;
import com.musicshop.services.inventory.InventoryService;
import com.musicshop.services.music.MusicService;
import com.musicshop.services.order.OrderServiceInterface;
import com.musicshop.services.storage.FileStorageService;
import com.musicshop.services.user.AuthenticationService;
import com.musicshop.services.user.UserService;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDateTime;
import java.io.Console;
import java.util.stream.Collectors;

public class MainMenu {
    private final MusicService musicService;
    private final InventoryService inventoryService;
    private final OrderServiceInterface orderService;
    private final AuthenticationService authService;
    private final UserService userService;
    private final AuthenticationService.WorkLogService workLogService;
    private final AnalyticsService analyticsService;
    private final Scanner scanner;
    private final Console console = System.console();

    private final FileStorageService fileStorageService;

    public MainMenu(MusicService musicService, InventoryService inventoryService,
                    OrderServiceInterface orderService, AuthenticationService authService,
                    UserService userService, AuthenticationService.WorkLogService workLogService,
                    AnalyticsService analyticsService, FileStorageService fileStorageService) {
        this.musicService = musicService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
        this.authService = authService;
        this.userService = userService;
        this.workLogService = workLogService;
        this.analyticsService = analyticsService;
        this.scanner = new Scanner(System.in);
        this.fileStorageService = fileStorageService;
    }

    // start function that checks
    public void start() {
        try {
            while (true) {
                if (!authService.isAuthenticated()) {
                    if (!showLoginMenu()) {
                        break;
                    }
                } else {
                    showRoleBasedMenu();
                }
            }
        } finally {
            if (authService.isAuthenticated()) {
                logout();
            }
        }
    }

    private boolean showLoginMenu() {
        while (true) {
            System.out.println("\n=== Music Shop Management System - Login ===");
            System.out.println("1. Login");
            System.out.println("2. Create an Account");
            System.out.println("3. Quit");
            System.out.print("Choose an option: ");

            int choice = getUserChoice();
            
            switch (choice) {
                case 1:
                    if (performLogin()) { return true; }
                    break;
                case 2:
                    createAccount();
                    break;
                case 3:
                    System.out.println("Exiting program. Goodbye!");
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void searchInventory() {
        System.out.println("\n=== Advanced Search ===");

        // Keyword input
        System.out.print("Enter search keyword (or press Enter to skip): ");
        String keyword = scanner.nextLine();
        keyword = keyword.isEmpty() ? null : keyword;

        // Minimum price input with validation
        Double minPrice = null;
        while (minPrice == null) {
            System.out.print("Enter minimum price (or press Enter to skip): ");
            String minPriceStr = scanner.nextLine();
            if (minPriceStr.isEmpty()) {
                break;  // Skip if no input
            }
            try {
                minPrice = Double.parseDouble(minPriceStr);
                if (minPrice < 0) {
                    throw new IllegalArgumentException("Price must not be negative.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input for minimum price. Please enter a non-negative number.");
                minPrice = null;  // Reset to null to re-prompt
            }
        }

        // Maximum price input with validation
        Double maxPrice = null;
        while (maxPrice == null) {
            System.out.print("Enter maximum price (or press Enter to skip): ");
            String maxPriceStr = scanner.nextLine();
            if (maxPriceStr.isEmpty()) {
                break;  // Skip if no input
            }
            try {
                maxPrice = Double.parseDouble(maxPriceStr);
                if (maxPrice < 0) {
                    throw new IllegalArgumentException("Price must not be negative.");
                }
                if (minPrice != null && maxPrice < minPrice) {
                    throw new IllegalArgumentException("Maximum price cannot be less than minimum price.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input for maximum price. Please enter a non-negative number greater than or equal to the minimum price.");
                maxPrice = null;  // Reset to null to re-prompt
            }
        }

        // Item type selection with validation
        System.out.println("Select item type:");
        System.out.println("1. All");
        System.out.println("2. Album");
        System.out.println("3. Instrument");
        int typeChoice = getUserChoice();
        String itemType = switch (typeChoice) {
            case 2 -> "Album";
            case 3 -> "Instrument";
            default -> null;  // Reset to re-prompt
        };

        // In-stock status selection with validation
        System.out.println("Show in-stock items only?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.println("3. Show all");
        int stockChoice = getUserChoice();
        Boolean inStock = switch (stockChoice) {
            case 1 -> true;
            case 2 -> false;
            default -> null;
        };

        // Build search criteria and perform search
        SearchCriteria criteria = new SearchCriteria(keyword, minPrice, maxPrice, itemType, inStock);
        List<MusicItem> results = inventoryService.searchItems(criteria);

        // Display search results
        if (results.isEmpty()) {
            System.out.println("No items found matching your criteria.");
        } else {
            //System.out.println("\n=== Search Results ===");
            //results.forEach(System.out::println);

            // Ask the user how they want to sort the results
            System.out.println("\nHow would you like to sort the results?");
            System.out.println("1. By type");
            System.out.println("2. By name");
            System.out.println("3. By price");
            int sortChoice = getUserChoice();

            switch (sortChoice) {
                case 1:
                    // Sort by type
                    results.sort(Comparator.comparing(MusicItem::getType));
                    break;
                case 2:
                    // Sort by name
                    results.sort(Comparator.comparing(MusicItem::getName));
                    break;
                case 3:
                    // Sort by price
                    results.sort(Comparator.comparing(MusicItem::getPrice));
                    break;
                default:
                    System.out.println("Invalid choice. No sorting applied.");
            }

            // Display the sorted results
            System.out.println("\n=== Sorted Results ===");
            results.forEach(System.out::println);
        }
    }

    private boolean performLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        char[] passwordArr = console.readPassword("Password: ");
        String password = new String(passwordArr);

        try {
            authService.login(username, password);
            System.out.println("Login successful!");
            workLogService.checkIn(authService.getCurrentUser().getId());
            return true;
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
            return false;
        }
    }

    private void createUserPrompt(String successMessage, String errorMessage) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        System.out.println("Select role:");
        System.out.println("1. Admin");
        System.out.println("2. Shop Employee");
        System.out.println("3. Accountant");

        int roleChoice = getUserChoice();
        UserRole role;
        switch (roleChoice) {
            case 1 -> role = UserRole.ADMIN;
            case 2 -> role = UserRole.SHOP_EMPLOYEE;
            case 3 -> role = UserRole.ACCOUNTANT;
            default -> {
                System.out.println("Invalid role selected");
                return;
            }
        }

        try {
            userService.createUser(username, password, role);
            System.out.println(successMessage);
        } catch (IllegalArgumentException e) {
            System.out.println(errorMessage + ": " + e.getMessage());
        }
    }

    private void createAccount() {
        System.out.println("\n=== Create an Account ===");
        createUserPrompt("Account created successfully! Please log in.", "Error creating account");
    }

    private void addNewUser() {
        createUserPrompt("User created successfully!", "Error creating user");
    }

    private void showRoleBasedMenu() {
        User currentUser = authService.getCurrentUser();
        switch (currentUser.getRole()) {
            case ADMIN -> showAdminMenu();
            case SHOP_EMPLOYEE -> showEmployeeMenu();
            case ACCOUNTANT -> showAccountantMenu();
        }
    }

    // input validation getUserChoice()
    private int getUserChoice() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Please enter a number.");
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void editItem() {
        System.out.print("Enter the name of the item to modify: ");
        String itemName = scanner.nextLine();

        MusicItem item = inventoryService.findItemByName(itemName);
        if (item == null) {
            System.out.println("Item not found in inventory.");
            return;
        }

        System.out.println("Current item details: " + item);
        System.out.println("Current Barcode: " + item.getBarcode());  // Display barcode
        boolean modifying = true;
        while (modifying) {
            System.out.println("Choose what to modify:");
            System.out.println("1. Name");
            System.out.println("2. Price");
            System.out.println("3. Quantity");
            if (item instanceof Album) {
                System.out.println("4. Artist");
                System.out.println("5. Year");
            }
            System.out.println("6. Done");

            int choice = getUserChoice();
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    item.setName(newName);
                    System.out.println("Name updated to: " + newName);
                }
                case 2 -> {
                    System.out.print("Enter new price: ");
                    try {
                        double newPrice = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline
                        if (newPrice > 0) {
                            item.setPrice(newPrice);
                            System.out.println("Price updated to: $" + newPrice);
                        } else {
                            System.out.println("Invalid price. Price must be greater than zero.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                        scanner.nextLine(); // Clear invalid input
                    }
                }
                case 3 -> {
                    System.out.print("Enter new quantity: ");
                    try {
                        int newQuantity = getUserChoice();
                        scanner.nextLine(); // Consume newline
                        if (newQuantity > 0) {
                            item.setQuantity(newQuantity);
                            System.out.println("Quantity updated to: " + newQuantity);
                        } else {
                            System.out.println("Invalid quantity. Quantity must be greater than zero.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                        scanner.nextLine(); // Clear invalid input
                    }
                }
                case 6 -> {
                    modifying = false;
                    inventoryService.saveItemsInInventory();
                    System.out.println("Item modifications saved.");
                }
                default -> System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private String generateBarcode() {
        // Simple barcode generation example: can use libraries like Zing for actual barcode generation
        return UUID.randomUUID().toString();  // Or any logic to generate a unique barcode
    }

    private String getBarcodeFromUser() {
        System.out.println("Do you want to generate a random barcode or enter your own?");
        System.out.println("1. Generate random barcode");
        System.out.println("2. Enter your own barcode");
        int choice = getUserChoice();

        if (choice == 1) {
            return generateBarcode();  // Generate a random barcode
        } else if (choice == 2) {
            System.out.print("Enter custom barcode: ");
            return scanner.nextLine();  // User provides their own barcode
        } else {
            System.out.println("Invalid option. Generating a random barcode by default.");
            return generateBarcode();  // Default to random barcode if invalid input
        }
    }

    public void addItem() {
        System.out.println("Choose item type: 1. Instrument  2. Album");
        int type = getUserChoice();

        try {
            String barcode = getBarcodeFromUser();  // Generate barcode

            if (type == 1) {
                String type_selected_jsonID = "instrument";

                System.out.print("Enter Instrument name: ");
                String name = scanner.nextLine();
                System.out.print("Enter Instrument price: ");
                double price = scanner.nextDouble();
                scanner.nextLine();  // Consume newline
                //System.out.print("Enter Instrument type (e.g., Guitar, Piano): ");

                MusicItem instrument = new Instrument(name, price, type_selected_jsonID, 1, barcode);
                musicService.addItem(instrument);

                System.out.println("Instrument added to inventory with barcode: " + barcode);

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
                int releaseYear = getUserChoice();
                scanner.nextLine();  // Consume newline

                MusicItem album = new Album(name, price, artist, releaseYear, type_selected_jsonID, 1, barcode);
                musicService.addItem(album);

                System.out.println("Album added to inventory with barcode: " + barcode);
            } else {
                throw new InvalidItemTypeException("Invalid item type selected.");
            }
        } catch (InvalidItemTypeException | InvalidItemException | InputMismatchException e) {
            System.out.println("Failed to add item: " + e.getMessage());
            scanner.nextLine();  // Clear invalid input if any
        }
    }

    public void removeItem(MusicItem item) {
        // If the item is found, remove it from the inventory
        if (item != null) {
            // Remove the item directly from the inventory list in the service
            inventoryService.getInventory().remove(item);
            inventoryService.saveItemsInInventory(); // Save after removal
            System.out.println("Item '" + item.getName() + "' has been removed from the inventory.");
        } else {
            System.out.println("Item not found in the inventory.");
        }
    }


    public void removeItem() {
        // Ask the user for the name or barcode of the item to remove
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the name or barcode of the item to remove:");

        String input = scanner.nextLine().trim();
        MusicItem itemToRemove;

        // Try finding the item by name first
        itemToRemove = inventoryService.findItemByName(input);

        // If not found by name, try finding by barcode
        if (itemToRemove == null) {
            itemToRemove = inventoryService.getItems().stream()
                    .filter(item -> item.getBarcode().equals(input))
                    .findFirst()
                    .orElse(null);
        }

        // If item is found, remove it and save the updated inventory
        if (itemToRemove != null) {
            // Remove the item directly from the inventory list in the service
            inventoryService.getInventory().remove(itemToRemove);
            inventoryService.saveItemsInInventory(); // Save after removal
            System.out.println("Item '" + itemToRemove.getName() + "' has been removed from the inventory.");
        } else {
            System.out.println("Item not found in the inventory.");
        }
    }
    public void viewItems() {
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
                removeItem(item);// remove item from inventory
                order.addItem(item);
                System.out.println(item.getName() + " added to cart.");
            } else {
                System.out.println("Invalid selection.");
            }
        }

        User currentEmployeeProcessingOrder = authService.getCurrentUser();

        if (currentEmployeeProcessingOrder != null) {
            orderService.processOrder(order, currentEmployeeProcessingOrder);
            System.out.println("Order processed and saved successfully.");
        } else {
            System.out.println("Error: No authenticated user to process the order.");
        }
    }

    public void viewOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
        } else {
            System.out.println("\n--- Order List ---");
            orders.forEach(System.out::println);
        }
    }

    private void showAdminMenu() {
        System.out.println("\n=== Admin Dashboard ===");
        System.out.println("1. Manage Users");
        System.out.println("2. View System Stats");
        System.out.println("3. View Inventory");
        System.out.println("4. Generate Reports");
        System.out.println("5. View Users Work Hours");
        System.out.println("0. Logout");

        int choice = getUserChoice();
        switch (choice) {
            case 1 -> manageUsers();
            case 2 -> viewSystemStats();
            case 3 -> viewItems();
            case 4 -> generateReports();
            case 5 -> viewAllWorkLogs();
            case 0 -> logout();
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void showEmployeeMenu() {
        System.out.println("\n=== Employee Dashboard ===");
        System.out.println("1. View Inventory");
        System.out.println("2. Search Inventory");
        System.out.println("3. Add Item");
        System.out.println("4. Remove Item");
        System.out.println("5. Create Order");
        System.out.println("6. View Orders");
        System.out.println("7. Edit Item");
        //System.out.println("//8. Update Order Status");  // New option for updating order status
        System.out.println("8. View My Work Hours");
        System.out.println("0. Logout");

        int choice = getUserChoice();
        switch (choice) {
            case 1 -> viewItems();
            case 2 -> searchInventory();
            case 3 -> addItem();
            case 4 -> removeItem();
            case 5 -> createOrder();
            case 6 -> viewOrders();
            case 7 -> editItem();
            //case 8 -> updateOrderStatus();  // Call the new method
            case 8 -> viewMyWorkHours();
            case 0 -> logout();
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void showAccountantMenu() {
        System.out.println("\n=== Accountant Dashboard ===");
        System.out.println("1. View Sales Reports");
        System.out.println("2. View Inventory Status");
        System.out.println("3. View Revenue Analysis");
        System.out.println("4. View Top Selling Items");
        System.out.println("5. Export Reports");
        System.out.println("0. Logout");

        int choice = getUserChoice();
        switch (choice) {
            case 1 -> viewSalesReports();
            case 2 -> viewInventoryStatus();
            case 3 -> viewRevenueAnalysis();
            case 4 -> viewTopSellingItems();
            case 5 -> exportReports();
            case 0 -> logout();
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    // Admin functions
    private void manageUsers() {
        System.out.println("\n=== User Management ===");
        System.out.println("1. View All Users");
        System.out.println("2. Add New User");
        System.out.println("3. Modify User");
        System.out.println("4. Deactivate User");
        System.out.println("0. Back");

        int choice = getUserChoice();
        switch (choice) {
            case 1 -> {
                List<User> users = userService.getAllUsers();
                users.forEach(user -> System.out.println(user));  // Now this will print using the formatted toString
            }
            case 2 -> addNewUser();
            case 3 -> modifyUser();
            case 4 -> deactivateUser();
        }
    }

    private void modifyUser() {
        System.out.print("Enter username to modify: ");
        String username = scanner.nextLine();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Current role: " + user.getRole());
            System.out.println("Select new role (or 0 to keep current):");
            System.out.println("1. Admin");
            System.out.println("2. Shop Employee");
            System.out.println("3. Accountant");

            int roleChoice = getUserChoice();
            if (roleChoice > 0 && roleChoice <= 3) {
                UserRole newRole = switch (roleChoice) {
                    case 1 -> UserRole.ADMIN;
                    case 2 -> UserRole.SHOP_EMPLOYEE;
                    case 3 -> UserRole.ACCOUNTANT;
                    default -> user.getRole();
                };
                user.setRole(newRole);
                userService.updateUser(user);
                System.out.println("User updated successfully!");
            }
        } else {
            System.out.println("User not found.");
        }
    }

    private void deactivateUser() {
        System.out.print("Enter username to deactivate: ");
        String username = scanner.nextLine();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setActive(false);
            userService.updateUser(user);
            System.out.println("User deactivated successfully!");
        } else {
            System.out.println("User not found.");
        }
    }

    // Employee functions
    private void viewMyWorkHours() {
        String userId = authService.getCurrentUser().getId();
        Duration totalWork = workLogService.getTotalWorkTime(userId);
        System.out.println("Total work hours: " + 
                totalWork.toHours() + " hours, " + 
                totalWork.toMinutesPart() + " minutes");
    }

    // Accountant functions
    private void viewSalesReports() {
        System.out.println("\n=== Sales Reports ===");
        System.out.println("1. Daily Report");
        System.out.println("2. Weekly Report");
        System.out.println("3. Monthly Report");

        int choice = getUserChoice();
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = switch (choice) {
            case 1 -> endDate.minusDays(1);
            case 2 -> endDate.minusWeeks(1);
            case 3 -> endDate.minusMonths(1);
            default -> {
                System.out.println("Invalid choice");
                yield null;
            }
        };

        if (startDate != null) {
            SalesReport report = analyticsService.generateSalesReport(startDate, endDate);
            displaySalesReport(report);
        }
    }

    private void viewInventoryStatus() {
        Map<String, Integer> status = analyticsService.getInventoryStatus();
        System.out.println("\n=== Inventory Status ===");
        status.forEach((type, count) -> 
                System.out.println(type + ": " + count + " items"));
    }

    private void viewRevenueAnalysis() {
        SalesReport report = analyticsService.generateSalesReport(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now()
                );
        System.out.println("\n=== Revenue Analysis ===");
        System.out.println("Total Revenue: $" + report.getTotalRevenue());
        System.out.println("\nRevenue by Category:");
        report.getRevenueByCategory().forEach((category, revenue) ->
                System.out.println(category + ": $" + revenue));
    }

    private void viewTopSellingItems() {
        SalesReport report = analyticsService.generateSalesReport(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now()
        );
        System.out.println("\n=== Top Selling Items ===");

        // Sort items by quantity in descending order and print
        report.getTopSellingItems().entrySet()
                .stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))  // Sort in descending order
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " units"));
    }

    private void exportReports() {
        try {
            ReportExportService exportService = new ReportExportService(
                analyticsService,
                userService,
                fileStorageService
            );
            
            String fileName = "Reports/monthly_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".pdf";
            
            exportService.generateMonthlyReport(fileName);
            System.out.println("Report exported successfully to: " + fileName);
        } catch (IOException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private void logout() {
        User currentLoggedInUser = authService.getCurrentUser();
        if (currentLoggedInUser != null){
            try {
                workLogService.checkOut(currentLoggedInUser.getId());
            } catch (Exception e) {
                System.out.println("Warning: Failed to check out work log: " + e.getMessage());
            } finally {
                authService.logout();
                System.out.println("Logged out successfully!");
            }
        }
    }

    private void displaySalesReport(SalesReport report) {
        System.out.println("\n=== Sales Report ===");
        System.out.println("Generated: " + report.getGeneratedDate());
        System.out.println("Total Orders: " + report.getTotalOrders());
        System.out.println("Total Revenue: $" + report.getTotalRevenue());

        System.out.println("\nTop Selling Items:");
        report.getTopSellingItems().forEach((item, quantity) ->
                System.out.println(item + ": " + quantity + " units"));

        System.out.println("\nRevenue by Category:");
        report.getRevenueByCategory().forEach((category, revenue) ->
                System.out.println(category + ": $" + revenue));
    }
    // Add these methods to MainMenu.java
    private void viewSystemStats() {
        System.out.println("\n=== System Statistics ===");
        System.out.println("Total Users: " + userService.getAllUsers().size());
        System.out.println("Total Items in Inventory: " + inventoryService.getItems().size());
        System.out.println("Total Orders: " + orderService.getAllOrders().size());
    }

    private void generateReports() {
        System.out.println("\n=== Generate Reports ===");
        System.out.println("1. User Activity Report");
        System.out.println("2. Inventory Report");

        int choice = getUserChoice();
        switch (choice) {
            case 1 -> generateUserActivityReport();
            case 2 -> generateInventoryReport();
                default -> System.out.println("Invalid option selected.");
        }
    }

    private void generateUserActivityReport() {
        // Get all work logs as a Map of userId -> Duration
        Map<String, java.time.Duration> allWorkLogs = workLogService.getAllWorkLogs();

        // Print header
        System.out.println("\n=== User Activity Report ===");

        // Iterate over all work logs (userId -> Duration)
        allWorkLogs.forEach((userId, totalDuration) -> {
            // Fetch user details
            Optional<User> user = userService.findById(userId);

            // Calculate the total hours, minutes, and seconds
            long totalSeconds = totalDuration.getSeconds();
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            // Calculate how many times this user has worked (in terms of check-ins)
            long workSessions = allWorkLogs.keySet().stream()
                    .filter(userId::equals)  // Filter for this userId
                    .count();

            // Print the user info
            System.out.println("User: " + (user.isPresent() ? user.get().getUsername() : "Unknown"));
            System.out.println("Times Served: " + workSessions);
            System.out.printf("Total Duration: %02d Hours %02d Minutes %02d Seconds%n", hours, minutes, seconds);
            System.out.println("-----------");
        });
    }

    private void generateInventoryReport() {
        // Get the list of orders from the order service
        List<Order> orders = orderService.getAllOrders();

        // Create a map to hold the sold quantities by item name
        Map<String, Long> soldItems = new HashMap<>();

        // Calculate the sold quantities from orders
        orders.forEach(order -> {
            order.getCartItems().forEach(item -> {
                String itemName = item.getName();
                int quantitySold = item.getQuantity(); // Get the sold quantity directly from the MusicItem

                // Update the sold quantities for each item
                soldItems.put(itemName, soldItems.getOrDefault(itemName, 0L) + quantitySold);
            });
        });

        // Get the list of items in the inventory (list of MusicItem objects)
        List<MusicItem> items = inventoryService.getItems();

        // Print the report
        System.out.println("\n=== Inventory Report ===");

        // Print Sold Items
        System.out.println("\n--- Sold Items ---");
        soldItems.forEach((item, quantity) -> {
            System.out.println(item + ": " + quantity + " units sold");
        });

        // Print Inventory Items
        System.out.println("\n--- Inventory Items ---");
        items.forEach(item -> {
            System.out.println(item.getName() + ": " + item.getQuantity() + " units in stock");
        });

        // Calculate remaining inventory (subtract sold from inventory)
        System.out.println("\n--- Remaining Inventory ---");
        items.forEach(item -> {
            String itemName = item.getName();
            int stockQuantity = item.getQuantity();
            long soldQuantity = soldItems.getOrDefault(itemName, 0L);
            long remainingQuantity = stockQuantity - soldQuantity;

            System.out.println(itemName + ": " + remainingQuantity + " units remaining");
        });
    }

    private void viewAllWorkLogs() {
        System.out.println("\n=== All Work Logs ===");
        Map<String, java.time.Duration> allWorkLogs = workLogService.getAllWorkLogs();
        allWorkLogs.forEach((userId, duration) -> {
            Optional<User> user = userService.findById(userId);
            System.out.println("User: " + (user.isPresent() ? user.get().getUsername() : "Unknown") +
                    " - Hours: " + duration.toHours() +
                    " Minutes: " + duration.toMinutesPart());
        });
    } 
}
