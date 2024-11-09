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
