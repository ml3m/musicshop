package com.musicshop.services;

import com.musicshop.models.Order;
import java.util.List;

public interface OrderServiceInterface {
    void processOrder(Order order);
    List<Order> getAllOrders();
    Order findOrderByCustomerId(String customerId);
    void saveOrders(List<Order> orders);
}
