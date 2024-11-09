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
}