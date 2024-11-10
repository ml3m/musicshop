package com.musicshop.services.analytics_dashboard;

import com.musicshop.models.music.MusicItem;
import com.musicshop.models.sales.Order;
import com.musicshop.models.sales.SalesReport;
import com.musicshop.services.inventory.InventoryService;
import com.musicshop.services.order.OrderServiceInterface;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsService {
    private final OrderServiceInterface orderService;
    private final InventoryService inventoryService;

    public AnalyticsService(OrderServiceInterface orderService, InventoryService inventoryService) {
        this.orderService = orderService;
        this.inventoryService = inventoryService;
    }

    public SalesReport generateSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderService.getAllOrders().stream()
                .filter(order -> {
                    // Assuming Order class has getOrderDate()
                    LocalDateTime orderDate = order.getOrderDate();
                    return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        SalesReport report = new SalesReport();
        report.setTotalOrders(orders.size());
        report.setTotalRevenue(calculateTotalRevenue(orders));

        // Calculate top-selling items
        Map<String, Integer> itemSales = new HashMap<>();
        orders.forEach(order -> {
            order.getCartItems().forEach(item -> {
                itemSales.merge(item.getName(), 1, Integer::sum);
            });
        });

        // Sort by value and get top 10
        Map<String, Integer> topSelling = itemSales.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));

        report.getTopSellingItems().putAll(topSelling);

        // Calculate revenue by category
        Map<String, Double> revenueByType = new HashMap<>();
        orders.forEach(order -> {
            order.getCartItems().forEach(item -> {
                revenueByType.merge(item.getType(), item.getPrice(), Double::sum);
            });
        });
        report.getRevenueByCategory().putAll(revenueByType);

        return report;
    }

    private double calculateTotalRevenue(List<Order> orders) {
        return orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public Map<String, Integer> getInventoryStatus() {
        return inventoryService.getItems().stream()
                .collect(Collectors.groupingBy(
                    MusicItem::getType,
                    Collectors.collectingAndThen(
                        Collectors.counting(),
                        Long::intValue
                    )
                ));
    }

    public List<MusicItem> getLowStockItems(int threshold) {
        // This would require adding stock quantity to MusicItem
        // For now, just return empty list
        return new ArrayList<>();
    }
}
