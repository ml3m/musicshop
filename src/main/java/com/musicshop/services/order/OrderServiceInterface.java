package com.musicshop.services.order;

import com.musicshop.models.sales.Order;
import com.musicshop.models.user.User;
import java.util.List;

public interface OrderServiceInterface {
    void processOrder(Order order);
    void processOrder(Order order, User employee);  // New method signature
    List<Order> getAllOrders();
    Order findOrderByCustomerId(String customerId);
    void saveOrders(List<Order> orders);
}