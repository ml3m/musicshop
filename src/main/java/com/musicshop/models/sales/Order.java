package com.musicshop.models.sales;

import com.musicshop.models.music.MusicItem;
import com.musicshop.models.user.Customer;
import com.musicshop.models.sales.OrderStatuses;

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
    private OrderStatuses status;

    // Default constructor (needed for Jackson to deserialize)
    public Order() {
    }

    public Order(Customer customer) {
        this.orderId = "ORD-" + UUID.randomUUID();
        this.customer = customer;
        this.cartItems = new ArrayList<>();
        this.totalAmount = 0.0;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatuses.PENDING;
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
    public OrderStatuses getStatus() { return status; }
    public void setStatus(OrderStatuses status) { this.status = status; }

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
}