package com.musicshop;

import com.musicshop.models.*;
import com.musicshop.services.*;
import com.musicshop.exceptions.*;

import java.time.Duration;
import java.util.*;
import java.time.LocalDateTime;

public class MainMenu {
    private final MusicServiceImpl musicService;
    private final InventoryServiceImpl inventoryService;
    private final OrderServiceInterface orderService;
    private final AuthenticationService authService;
    private final UserService userService;
    private final WorkLogService workLogService;
    private final AnalyticsService analyticsService;
    private final Scanner scanner;

    public MainMenu(MusicServiceImpl musicService, InventoryServiceImpl inventoryService, 
            OrderServiceInterface orderService, AuthenticationService authService,
            UserService userService, WorkLogService workLogService,
            AnalyticsService analyticsService) {
        this.musicService = musicService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
        this.authService = authService;
        this.userService = userService;
        this.workLogService = workLogService;
        this.analyticsService = analyticsService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            if (!authService.isAuthenticated()) {
                showLoginMenu();
            } else {
                showRoleBasedMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n=== Music Shop Management System - Login ===");
        System.out.println("1. Login");
        System.out.println("2. Create an Account");
        System.out.println("3. Quit");
        System.out.print("Choose an option: ");

        int choice = getUserChoice();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1 -> performLogin();
            case 2 -> createAccount();
            case 3 -> {
                System.out.println("Exiting program. Goodbye!");
                System.exit(0); // Terminates the program
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void performLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            authService.login(username, password);
            System.out.println("Login successful!");
            workLogService.checkIn(authService.getCurrentUser().getId());
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void createAccount() {
        System.out.println("\n=== Create an Account ===");
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
            System.out.println("Account created successfully! Please log in.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    private void showRoleBasedMenu() {
        User currentUser = authService.getCurrentUser();
        switch (currentUser.getRole()) {
            case ADMIN -> showAdminMenu();
            case SHOP_EMPLOYEE -> showEmployeeMenu();
            case ACCOUNTANT -> showAccountantMenu();
        }
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

    private void editItem() {
        System.out.print("Enter the name of the item to modify: ");
        scanner.nextLine(); // Consume newline
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
                        int newQuantity = scanner.nextInt();
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
        // Simple barcode generation example: can use libraries like ZXing for actual barcode generation
        return UUID.randomUUID().toString();  // Or any logic to generate a unique barcode
    }

    private String getBarcodeFromUser() {
        System.out.println("Do you want to generate a random barcode or enter your own?");
        System.out.println("1. Generate random barcode");
        System.out.println("2. Enter your own barcode");
        int choice = getUserChoice();

        scanner.nextLine();  // Consume newline

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

    private void addItem() {
        System.out.println("Choose item type: 1. Instrument  2. Album");
        int type = getUserChoice();
        scanner.nextLine();  // Consume newline

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
                int releaseYear = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                //System.out.print("Enter Album type (e.g., Vinyl, CD): ");

                MusicItem album = new Album(name, price, artist, releaseYear, type_selected_jsonID, 1, barcode);
                musicService.addItem(album);

                System.out.println("Album added to inventory with barcode: " + barcode);
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

        User currentEmployeeProcessingOrder = authService.getCurrentUser();

        if (currentEmployeeProcessingOrder != null){
            orderService.processOrder(order, currentEmployeeProcessingOrder);
            orderService.saveOrders(orderService.getAllOrders());
            System.out.println("Order processed and saved successfully.");
        } else {
            System.out.println("Error: No authenticated user to process the order.");
        }
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

    private void showAdminMenu() {
        System.out.println("\n=== Admin Dashboard ===");
        System.out.println("1. Manage Users");
        System.out.println("2. View System Stats");
        System.out.println("3. View Inventory");
        System.out.println("4. Generate Reports");
        System.out.println("5. View All Work Logs");
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
        System.out.println("2. Add Item");
        System.out.println("3. Remove Item");
        System.out.println("4. Create Order");
        System.out.println("5. View Orders");
        System.out.println("6. Edit Item Quantity");  // New option for editing item quantity
        System.out.println("7. View My Work Hours");
        System.out.println("0. Logout");

        int choice = getUserChoice();
        switch (choice) {
            case 1 -> viewItems();
            case 2 -> addItem();
            case 3 -> removeItem();
            case 4 -> createOrder();
            case 5 -> viewOrders();
            case 6 -> editItem();  // Call the new method
            case 7 -> viewMyWorkHours();
            case 0 -> logout();
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void editItemQuantity() {
        System.out.print("Enter the name of the item to modify: ");
        scanner.nextLine(); // Consume newline
        String itemName = scanner.nextLine();

        MusicItem item = inventoryService.findItemByName(itemName);
        if (item == null) {
            System.out.println("Item not found in inventory.");
            return;
        }

        System.out.println("Current quantity of " + itemName + ": " + item.getQuantity());
        System.out.print("Enter the new quantity: ");
        int newQuantity;
        try {
            newQuantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (newQuantity > 0) {
                inventoryService.editItemQuantity(itemName, newQuantity);  // Call the method to update quantity
            } else {
                System.out.println("Invalid quantity. Quantity must be greater than zero.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Clear invalid input
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
                users.forEach(System.out::println);
            }
            case 2 -> addNewUser();
            case 3 -> modifyUser();
            case 4 -> deactivateUser();
        }
    }

    private void addNewUser() {
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
            System.out.println("User created successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating user: " + e.getMessage());
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
        report.getTopSellingItems().forEach((item, quantity) ->
                System.out.println(item + ": " + quantity + " units"));
    }

    private void exportReports() {
        // Implementation for exporting reports to files
        System.out.println("Exporting reports... (To be implemented)");
    }

    private void logout() {
        try {
            workLogService.checkOut(authService.getCurrentUser().getId());
        } catch (IllegalStateException e) {
            System.out.println("Warning: " + e.getMessage());
        }
        authService.logout();
        System.out.println("Logged out successfully!");
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
        System.out.println("3. Sales Report");

        int choice = getUserChoice();
        switch (choice) {
            case 1 -> System.out.println("Generating User Activity Report...");
            case 2 -> System.out.println("Generating Inventory Report...");
            case 3 -> System.out.println("Generating Sales Report...");
                default -> System.out.println("Invalid option selected.");
        }
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
package com.musicshop.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private Customer customer;
    private List<MusicItem> cartItems;
    private double totalAmount;
    private LocalDateTime orderDate;
    private String processedById;  // User ID of the employee who processed the order
    private String processedBy;    // Name of the employee who processed the order
    private OrderStatus status;

    public enum OrderStatus {
        PENDING,
        PROCESSED,
        COMPLETED,
        CANCELLED
    }

    // Default constructor (needed for Jackson to deserialize)
    public Order() {
    }

    public Order(Customer customer) {
        this.orderId = "ORD-" + UUID.randomUUID();
        this.customer = customer;
        this.cartItems = new ArrayList<>();
        this.totalAmount = 0.0;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public void addItem(MusicItem item) {
        this.cartItems.add(item);
        this.totalAmount = calculateTotalAmount();
    }

    private double calculateTotalAmount() {
        return cartItems.stream().mapToDouble(MusicItem::getPrice).sum();
    }

    // Getters and setters
    public String getOrderId() { return orderId; }

    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<MusicItem> getCartItems() { return cartItems; }

    public void setCartItems(List<MusicItem> cartItems) { this.cartItems = cartItems; }

    public double getTotalAmount() { return totalAmount; }

    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderDate() { return orderDate; }

    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getProcessedById() { return processedById; }

    public void setProcessedById(String processedById) { this.processedById = processedById; }

    public String getProcessedBy() { return processedBy; }

    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }

    public OrderStatus getStatus() { return status; }

    public void setStatus(OrderStatus status) { this.status = status; }

    // Method to set both processor ID and name at once
    public void setProcessor(String id, String name) {
        this.processedById = id;
        this.processedBy = name;
    }

    @Override
    public String toString() {
        StringBuilder orderDetails = new StringBuilder();
        orderDetails.append("Order ID: ").append(orderId)
                .append("\nDate: ").append(orderDate)
                .append("\nCustomer: ").append(customer.getName())
                .append("\nStatus: ").append(status)
                .append("\nProcessed by: ").append(processedBy)
                .append(" (ID: ").append(processedById).append(")")
                .append("\nCart Items:");

        for (MusicItem item : cartItems) {
            orderDetails.append("\n  - ").append(item.getName())
                    .append(" (Price: $").append(item.getPrice())
                    .append(", Type: ").append(item.getType()).append(")");
        }

        orderDetails.append("\nTotal Amount: $").append(totalAmount);
        return orderDetails.toString();
    }
}package com.musicshop.models;

public enum UserRole {
    ADMIN,
    SHOP_EMPLOYEE,
    ACCOUNTANT
}
package com.musicshop.models;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Album.class, name = "album"),
    @JsonSubTypes.Type(value = Instrument.class, name = "instrument")
})

public abstract class MusicItem {
    protected String name;
    protected double price;
    protected int quantity;
    protected String barcode;

    public MusicItem() {
    }

    public MusicItem(String name, double price) {
        this(name, price, 1);  // Default quantity to 1
    }

    public MusicItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Price: $" + price + ", Quantity: " + quantity;
    }

    public abstract String getType();
}
package com.musicshop.models;

public class Customer {
    private String id;
    private String name;

    public Customer(){
    }

    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name;
    }
}
package com.musicshop.models;

public class Album extends MusicItem {
    private String artist;
    private final String type;
    private int year;

    public Album() {
        super(null, 0.0);
        this.type = "album";
    }

    public Album(String name, double price, String artist, int year, String type) {
        this(name, price, artist, year, type, 1, null);
    }

    public Album(String name, double price, String artist, int year, String type, int quantity, String barcode) {
        super(name, price, quantity);
        this.artist = artist;
        this.year = year;
        this.type = type;

        setBarcode(barcode);
    }

    @Override
    public String getType() {
        return type;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return super.toString() + ", Artist: " + artist + ", Year: " + year;
    }
}
package com.musicshop.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private String id;  // Randomly generated string ID
    private String username;
    private String password;
    private UserRole role;
    private boolean active;
    private LocalDateTime lastLogin;

    // Default constructor with random ID generation
    public User() {
        this.id = generateRandomId();
        this.active = true; // Default to active when created
    }

    // Constructor with user-defined fields and random ID generation
    public User(String username, String password, UserRole role) {
        this.id = generateRandomId();
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = true;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Setter for id if manual setting is required (optional)
    public void setId(String id) {
        this.id = id;
    }

    // Generate a random unique identifier using UUID
    private String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    // Other getters and setters remain the same
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
package com.musicshop.models;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;


public class SalesReport {
    private final String id;
    private final LocalDateTime generatedDate;
    private double totalRevenue;
    private int totalOrders;
    private final Map<String, Integer> topSellingItems;
    private final Map<String, Double> revenueByCategory;
    
    public SalesReport() {
        this.id = UUID.randomUUID().toString();
        this.generatedDate = LocalDateTime.now();
        this.topSellingItems = new HashMap<>();
        this.revenueByCategory = new HashMap<>();
    }

    // Getters and setters
    public String getId() { return id; }
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    public Map<String, Integer> getTopSellingItems() { return topSellingItems; }
    public Map<String, Double> getRevenueByCategory() { return revenueByCategory; }
}
package com.musicshop.models;

import java.time.LocalDateTime;
import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class WorkLog {
    private String userId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    @JsonIgnore // json ignore basically
    private transient Duration duration;

    public WorkLog(){}

    public WorkLog(String userId) {
        this.userId = userId;
        this.checkInTime = LocalDateTime.now();
    }

    public void checkOut() {
        this.checkOutTime = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public Duration getDuration() {
        if (checkOutTime == null) {
            return Duration.between(checkInTime, LocalDateTime.now());
        }
        return Duration.between(checkInTime, checkOutTime);
    }

    // Add getters for serialization if needed
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    // Add setters for deserialization if needed
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
package com.musicshop.models;

public class Instrument extends MusicItem {
    private final String type;

    public Instrument() {
        super(null, 0.0);
        this.type = "instrument";
    }

    public Instrument(String name, double price, String type) {
        this(name, price, type, 1, null);
    }

    public Instrument(String name, double price, String type, int quantity, String barcode) {
        super(name, price, quantity);
        this.type = type;
        setBarcode(barcode);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return super.toString() + ", Type: " + type;
    }
}
package com.musicshop.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
package com.musicshop.exceptions;

public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
package com.musicshop.exceptions;

public class InvalidItemException extends RuntimeException {
    public InvalidItemException(String message) {
        super(message);
    }
}
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
package com.musicshop.services;

import com.musicshop.models.MusicItem;
import com.musicshop.exceptions.InvalidItemException;

public class MusicServiceImpl implements MusicService {
    private final InventoryServiceImpl inventoryService;

    public MusicServiceImpl(InventoryServiceImpl inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void addItem(MusicItem item) {
        if (item == null || item.getPrice() < 0) {
            throw new InvalidItemException("Invalid item details");
        }
        inventoryService.addItem(item);
    }

    @Override
    public void removeItem(String itemName) {
        if (inventoryService.findItemByName(itemName) == null) {
            throw new InvalidItemException("Item not found in inventory");
        }
        inventoryService.removeItem(itemName);
    }
}

package com.musicshop.services;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.musicshop.models.Order;
import com.musicshop.models.MusicItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.musicshop.models.User;
import com.musicshop.models.WorkLog;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileStorageService {
    private static final String INVENTORY_FILE_PATH = "inventory.json";
    private static final String ORDERS_FILE_PATH = "orders.json";
    private static final String USERS_FILE_PATH = "users.json";
    private static final String WORKLOGS_FILE_PATH = "worklogs.json";
    private final ObjectMapper objectMapper;

    public FileStorageService() {
        this.objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        // Enable pretty printing
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Disable writing timestamps as strings if you prefer ISO date strings
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // Load work logs from the work logs JSON file
    public List<WorkLog> loadWorkLogs() {
        try {
            File file = new File(WORKLOGS_FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<WorkLog>>() {});
        } catch (IOException e) {
            System.out.println("Error loading work logs from JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Save work logs to the work logs JSON file
    public void saveWorkLogs(List<WorkLog> workLogs) {
        try {
            objectMapper.writeValue(new File(WORKLOGS_FILE_PATH), workLogs);
            System.out.println("Work logs saved to JSON file.");
        } catch (IOException e) {
            System.out.println("Error saving work logs to JSON: " + e.getMessage());
        }
    }

    // Load users from the users JSON file
    public List<User> loadUsers() {
        try {
            File file = new File(USERS_FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            System.out.println("Error loading users from JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Save users to the users JSON file
    public void saveUsers(List<User> users) {
        try {
            objectMapper.writeValue(new File(USERS_FILE_PATH), users);
            System.out.println("Users saved to JSON file.");
        } catch (IOException e) {
            System.out.println("Error saving users to JSON: " + e.getMessage());
        }
    }

    // Load items from the inventory JSON file
    public List<MusicItem> loadItems() {
        try {
            File file = new File(INVENTORY_FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<MusicItem>>() {});
        } catch (IOException e) {
            System.out.println("Error loading items from JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Save items to the inventory JSON file
    public void saveItems(List<MusicItem> items) {
        try {
            objectMapper.writeValue(new File(INVENTORY_FILE_PATH), items);
            System.out.println("Inventory saved to JSON file.");
        } catch (IOException e) {
            System.out.println("Error saving items to JSON: " + e.getMessage());
        }
    }

    // Append a single item to the inventory JSON file
    public void appendItem(MusicItem item) {
        try {
            // Load current inventory
            List<MusicItem> items = loadItems();
            // Add new item
            items.add(item);
            // Save updated inventory
            saveItems(items);
            System.out.println("Item appended to inventory.");
        } catch (Exception e) {
            System.out.println("Error appending item to inventory: " + e.getMessage());
        }
    }

    // Clear all items in the inventory JSON file
    public void clearAllItems() {
        try {
            saveItems(new ArrayList<>()); // Save an empty list to clear the file
            System.out.println("All items cleared from inventory.");
        } catch (Exception e) {
            System.out.println("Error clearing inventory: " + e.getMessage());
        }
    }

    // Load orders from orders JSON file
    public List<Order> loadOrders() {
        try {
            File file = new File(ORDERS_FILE_PATH);
            if (file.exists()) {
                return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));
            }
        } catch (IOException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public void saveOrders(List<Order> orders) {
        try {
            objectMapper.writeValue(new File(ORDERS_FILE_PATH), orders);
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }
}
package com.musicshop.services;

import com.musicshop.models.MusicItem;

public interface MusicService {
    void addItem(MusicItem item);
    void removeItem(String itemName);
}
package com.musicshop.services;

import com.musicshop.models.User;
import com.musicshop.models.UserRole;
import java.util.*;

public class UserService {
    private final FileStorageService fileStorageService;
    private final List<User> users;

    public UserService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.users = loadUsers();
        
        // Create default admin if no users exist
        if (users.isEmpty()) {
            createUser("admin", "admin123", UserRole.ADMIN);
        }
    }

    public User createUser(String username, String password, UserRole role) {
        if (findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User(username, password, role);
        users.add(user);
        saveUsers();
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public Optional<User> findById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public void updateUser(User user) {
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        saveUsers();
    }

    // not used.
    public void deleteUser(String userId) {
        users.removeIf(u -> u.getId().equals(userId));
        saveUsers();
    }

    private List<User> loadUsers() {
        return fileStorageService.loadUsers();
    }

    private void saveUsers() {
        fileStorageService.saveUsers(users);
    }
}
package com.musicshop.services;

import com.musicshop.models.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.musicshop.models.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderServiceImpl implements OrderServiceInterface {
    private static final String ORDER_FILE_PATH = "orders.json";
    private final ObjectMapper objectMapper;
    private final List<Order> orders;

    public OrderServiceImpl() {
        this.objectMapper = new ObjectMapper();
        // Configure ObjectMapper to handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Enable pretty printing
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.orders = loadOrders();
    }

    @Override
    public void processOrder(Order order) {
        orders.add(order);
        saveOrders(orders);
        System.out.println("Order processed for customer: " + order.getCustomer().getName());
    }

    @Override
    public void processOrder(Order order, User employee) {
        // Implementation of the new method with employee details
        order.setProcessor(employee.getId(), employee.getUsername());
        order.setStatus(Order.OrderStatus.PROCESSED);
        orders.add(order);
        saveOrders(orders);
        System.out.println("Order processed by employee: " + employee.getUsername());
    }

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    @Override
    public Order findOrderByCustomerId(String customerId) {
        return orders.stream()
                .filter(order -> order.getCustomer().getId().equals(customerId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void saveOrders(List<Order> orders) {
        try {
            objectMapper.writeValue(new File(ORDER_FILE_PATH), orders);
            System.out.println("Orders saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }

    private List<Order> loadOrders() {
        try {
            File file = new File(ORDER_FILE_PATH);
            if (file.exists()) {
                return objectMapper.readValue(file, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));
            }
        } catch (IOException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
package com.musicshop.services;

import com.musicshop.models.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsService {
    private final OrderServiceInterface orderService;
    private final InventoryService inventoryService;

    public AnalyticsService(OrderServiceInterface orderService, InventoryService inventoryService) {
        this.orderService = orderService;
        this.inventoryService = inventoryService;
    }

    public SalesReport generateSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderService.getAllOrders().stream()
                .filter(order -> {
                    // Assuming Order class has getOrderDate()
                    LocalDateTime orderDate = order.getOrderDate();
                    return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        SalesReport report = new SalesReport();
        report.setTotalOrders(orders.size());
        report.setTotalRevenue(calculateTotalRevenue(orders));

        // Calculate top-selling items
        Map<String, Integer> itemSales = new HashMap<>();
        orders.forEach(order -> {
            order.getCartItems().forEach(item -> {
                itemSales.merge(item.getName(), 1, Integer::sum);
            });
        });

        // Sort by value and get top 10
        Map<String, Integer> topSelling = itemSales.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));

        report.getTopSellingItems().putAll(topSelling);

        // Calculate revenue by category
        Map<String, Double> revenueByType = new HashMap<>();
        orders.forEach(order -> {
            order.getCartItems().forEach(item -> {
                revenueByType.merge(item.getType(), item.getPrice(), Double::sum);
            });
        });
        report.getRevenueByCategory().putAll(revenueByType);

        return report;
    }

    private double calculateTotalRevenue(List<Order> orders) {
        return orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public Map<String, Integer> getInventoryStatus() {
        return inventoryService.getItems().stream()
                .collect(Collectors.groupingBy(
                    MusicItem::getType,
                    Collectors.collectingAndThen(
                        Collectors.counting(),
                        Long::intValue
                    )
                ));
    }

    public List<MusicItem> getLowStockItems(int threshold) {
        // This would require adding stock quantity to MusicItem
        // For now, just return empty list
        return new ArrayList<>();
    }
}
package com.musicshop.services;

import com.musicshop.models.Order;
import com.musicshop.models.User;
import java.util.List;

public interface OrderServiceInterface {
    void processOrder(Order order);
    void processOrder(Order order, User employee);  // New method signature
    List<Order> getAllOrders();
    Order findOrderByCustomerId(String customerId);
    void saveOrders(List<Order> orders);
}package com.musicshop.services;

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
package com.musicshop.services;

import com.musicshop.models.Order;
import com.musicshop.models.Customer;
import com.musicshop.models.MusicItem;
import com.musicshop.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderService implements OrderServiceInterface {
    private static final String ORDER_FILE_PATH = "orders.json";
    private final ObjectMapper objectMapper;
    private final List<Order> orders;

    public OrderService() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.orders = loadOrders();
    }

    public void placeOrder(Customer customer, List<MusicItem> cartItems) {
        Order newOrder = new Order(customer);
        if (cartItems != null) {
            cartItems.forEach(newOrder::addItem);
        }
        processOrder(newOrder);
        System.out.println("Order placed successfully.");
    }

    @Override
    public void processOrder(Order order) {
        orders.add(order);
        saveOrders(orders);
        System.out.println("Order processed for customer: " + order.getCustomer().getName());
    }

    // New method to process order with employee information
    public void processOrder(Order order, User employee) {
        order.setProcessor(employee.getId(), employee.getUsername());
        order.setStatus(Order.OrderStatus.PROCESSED);
        orders.add(order);
        saveOrders(orders);
        System.out.println("Order processed by employee: " + employee.getUsername());
    }

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    @Override
    public Order findOrderByCustomerId(String customerId) {
        return orders.stream()
                .filter(order -> order.getCustomer().getId().equals(customerId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void saveOrders(List<Order> orders) {
        try {
            objectMapper.writeValue(new File(ORDER_FILE_PATH), orders);
            System.out.println("Orders saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }

    private List<Order> loadOrders() {
        try {
            File file = new File(ORDER_FILE_PATH);
            if (file.exists()) {
                return objectMapper.readValue(file, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));
            }
        } catch (IOException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
package com.musicshop.services;

import com.musicshop.models.MusicItem;
import java.util.List;

public interface InventoryService {
    List<MusicItem> getItems();
    MusicItem findItemByName(String name);
}
package com.musicshop.services;

import com.musicshop.models.User;
import com.musicshop.exceptions.AuthenticationException;
import com.musicshop.models.UserRole;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthenticationService {
    private final UserService userService;
    private User currentUser;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    public User login(String username, String password) throws AuthenticationException {
        Optional<User> user = userService.findByUsername(username);
        
        if (user.isPresent() && user.get().getPassword().equals(password)) {  // In real app, use password hashing
            if (!user.get().isActive()) {
                throw new AuthenticationException("Account is disabled");
            }
            user.get().setLastLogin(LocalDateTime.now());
            userService.updateUser(user.get());
            currentUser = user.get();
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

    public boolean hasRole(UserRole... roles) {
        if (!isAuthenticated()) return false;
        for (UserRole role : roles) {
            if (currentUser.getRole() == role) return true;
        }
        return false;
    }
}
package com.musicshop.services;

import com.musicshop.models.MusicItem;
import java.util.List;
import java.util.ArrayList;

public class InventoryServiceImpl implements InventoryService {
    private final List<MusicItem> inventory;
    private final FileStorageService fileStorageService;

    public InventoryServiceImpl(FileStorageService fileStorageService) {
        this.inventory = new ArrayList<>();
        this.fileStorageService = fileStorageService;

        // Load items from JSON into memory on initialization
        List<MusicItem> loadedItems = fileStorageService.loadItems();
        inventory.addAll(loadedItems); // Populate in-memory inventory
    }

    @Override
    public List<MusicItem> getItems() {
        return new ArrayList<>(inventory); // Return a copy to avoid external modifications
    }

    @Override
    public MusicItem findItemByName(String name) {
        return inventory.stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void addItem(MusicItem newItem) {
        MusicItem existingItem = findItemByName(newItem.getName());
        if (existingItem != null) {
            existingItem.increaseQuantity(newItem.getQuantity());
            System.out.println("Increased quantity of " + newItem.getName() + " to " + existingItem.getQuantity());
        } else {
            inventory.add(newItem);
            System.out.println("Added new item: " + newItem.getName());
        }
        fileStorageService.saveItems(inventory); // Save inventory after modification
    }

    public void editItemQuantity(String itemName, int newQuantity) {
        MusicItem item = findItemByName(itemName);
        if (item != null) {
            if (newQuantity > 0) {
                item.setQuantity(newQuantity);
                System.out.println("Quantity of " + itemName + " updated to " + newQuantity);
                fileStorageService.saveItems(inventory); // Save the inventory after modification
            } else {
                System.out.println("Invalid quantity. It must be greater than zero.");
            }
        } else {
            System.out.println("Item not found in inventory: " + itemName);
        }
    }

    public void removeItem(String itemName) {
        inventory.removeIf(item -> item.getName().equalsIgnoreCase(itemName));
        fileStorageService.saveItems(inventory); // Save full inventory after removal
    }

    public void clearItems() {
        inventory.clear();
        fileStorageService.clearAllItems(); // Clear the JSON file
    }

    // New method to save inventory using FileStorageService
    public void saveItemsInInventory() {
        fileStorageService.saveItems(inventory);
    }
}
