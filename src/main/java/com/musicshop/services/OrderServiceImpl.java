package com.musicshop.services;

import com.musicshop.models.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        this.orders = loadOrders();
    }

    @Override
    public void processOrder(Order order) {
        orders.add(order);
        saveOrders(orders);
        System.out.println("Order processed for customer: " + order.getCustomer().getName());
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
                return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));
            }
        } catch (IOException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
