package com.musicshop.models.sales;

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
