package com.musicshop.services.order;

import com.musicshop.models.sales.Order;
import com.musicshop.models.sales.OrderStatuses;
import com.musicshop.models.user.User;
import com.musicshop.services.storage.FileStorageService;

import java.util.ArrayList;
import java.util.List;

public class OrderService implements OrderServiceInterface {
    private final List<Order> orders;
    private final FileStorageService fileStorageService;

    public OrderService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.orders = fileStorageService.loadOrders();
    }

    @Override
    public void processOrder(Order order, User employee) {
        order.setProcessor(employee.getId(), employee.getUsername());
        order.setStatus(OrderStatuses.PROCESSED); // dafault
        orders.add(order);
        fileStorageService.saveOrders(orders);
        System.out.println("Order processed by employee: " + employee.getUsername());
    }

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
}
