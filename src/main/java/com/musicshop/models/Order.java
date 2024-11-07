package com.musicshop.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private Customer customer;
    private List<MusicItem> cartItems;
    private double totalAmount;

    // Default constructor (needed for Jackson to deserialize)
    public Order() {
    }

    public Order(Customer customer) {
        this.orderId = "ORD-" + UUID.randomUUID();
        this.customer = customer;
        this.cartItems = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    // Method to add an item to the order
    public void addItem(MusicItem item) {
        this.cartItems.add(item);
        this.totalAmount = calculateTotalAmount(); // Update total amount after adding item
    }

    // Method to calculate the total amount of the order
    private double calculateTotalAmount() {
        return cartItems.stream().mapToDouble(MusicItem::getPrice).sum();
    }

    public String getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<MusicItem> getCartItems() {
        return cartItems;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        StringBuilder orderDetails = new StringBuilder();
        orderDetails.append("Order ID: ").append(orderId)
                    .append("\nCustomer: ").append(customer.getName())
                    .append("\nCustomer ID: ").append(customer.getId())
                    .append("\nCart Items:");

        // Add cart items details with polymorphism
        for (MusicItem item : cartItems) {
            orderDetails.append("\n  - ").append(item.getName())
                        .append(" (Price: $").append(item.getPrice())
                        .append(", Type: ").append(item.getType()).append(")");
        }

        orderDetails.append("\nTotal Amount: $").append(totalAmount);

        return orderDetails.toString();
    }
}
