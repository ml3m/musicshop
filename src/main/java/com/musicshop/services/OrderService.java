package com.musicshop.services;

import com.musicshop.models.Order;
import com.musicshop.models.MusicItem;
import com.musicshop.models.Customer;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final FileStorageService fileStorageService;

    public OrderService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // Place an order
    public void placeOrder(Customer customer, List<MusicItem> cartItems) {
        Order newOrder = new Order(customer);
        if (cartItems != null) {
            cartItems.forEach(newOrder::addItem);
        }
        saveOrder(newOrder);
        System.out.println("Order placed successfully.");
    }

    // Save order to file
    private void saveOrder(Order order) {
        List<Order> orders = loadOrders();
        orders.add(order);
        fileStorageService.saveOrders(orders);  // No file path needed
    }

    // Load all orders
    public List<Order> loadOrders() {
        return fileStorageService.loadOrders();  // No file path needed
    }
}
