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
    private String processedBy;  // User ID of the employee who processed the order
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
    public Customer getCustomer() { return customer; }
    public List<MusicItem> getCartItems() { return cartItems; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    @Override
    public String toString() {
        StringBuilder orderDetails = new StringBuilder();
        orderDetails.append("Order ID: ").append(orderId)
                   .append("\nDate: ").append(orderDate)
                   .append("\nCustomer: ").append(customer.getName())
                   .append("\nStatus: ").append(status)
                   .append("\nProcessed by: ").append(processedBy)
                   .append("\nCart Items:");

        for (MusicItem item : cartItems) {
            orderDetails.append("\n  - ").append(item.getName())
                       .append(" (Price: $").append(item.getPrice())
                       .append(", Type: ").append(item.getType()).append(")");
        }

        orderDetails.append("\nTotal Amount: $").append(totalAmount);

        return orderDetails.toString();
    }
}
