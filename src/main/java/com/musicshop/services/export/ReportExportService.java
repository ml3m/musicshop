package com.musicshop.services.export;

import com.musicshop.models.sales.Order;
import com.musicshop.models.sales.SalesReport;
import com.musicshop.models.user.User;
import com.musicshop.models.user.WorkLog;
import com.musicshop.models.music.MusicItem;
import com.musicshop.services.analytics_dashboard.AnalyticsService;
import com.musicshop.services.user.UserService;
import com.musicshop.services.storage.FileStorageService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportExportService {
    private static final float MARGIN = 50;
    private static final float FONT_SIZE = 12;
    private static final float LEADING = 1.5f * FONT_SIZE;

    private final AnalyticsService analyticsService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public ReportExportService(
            AnalyticsService analyticsService,
            UserService userService,
            FileStorageService fileStorageService) {
        this.analyticsService = analyticsService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    public void generateMonthlyReport(String outputPath) throws IOException {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);
        
        // Gather all required data
        SalesReport salesReport = analyticsService.generateSalesReport(startDate, endDate);
        Map<String, Integer> inventoryStatus = analyticsService.getInventoryStatus();
        List<WorkLog> workLogs = fileStorageService.loadWorkLogs();
        List<User> employees = userService.getAllUsers();

        // Create PDF document
        try (PDDocument document = new PDDocument()) {
            generateReportContent(document, salesReport, inventoryStatus, workLogs, employees);
            document.save(outputPath);
        }
    }

    private void generateReportContent(
            PDDocument document,
            SalesReport salesReport,
            Map<String, Integer> inventoryStatus,
            List<WorkLog> workLogs,
            List<User> employees) throws IOException {
        
        // Create first page - Overview
        PDPage overviewPage = new PDPage(PDRectangle.A4);
        document.addPage(overviewPage);
        
        try (PDPageContentStream contentStream = new PDPageContentStream(document, overviewPage)) {
            float yPosition = overviewPage.getMediaBox().getHeight() - MARGIN;

            // Title and Date
            yPosition = addTitle(contentStream, "Monthly Business Report", yPosition);
            yPosition = addDateSection(contentStream, yPosition);
            
            // Sales Overview
            yPosition = addSalesOverview(contentStream, salesReport, yPosition);
            
            // Inventory Status
            yPosition = addInventoryStatus(contentStream, inventoryStatus, yPosition);
        }

        // Create second page - Detailed Analysis
        PDPage detailsPage = new PDPage(PDRectangle.A4);
        document.addPage(detailsPage);
        
        try (PDPageContentStream contentStream = new PDPageContentStream(document, detailsPage)) {
            float yPosition = detailsPage.getMediaBox().getHeight() - MARGIN;

            // Employee Work Summary
            yPosition = addEmployeeWorkSummary(contentStream, workLogs, employees, yPosition);
            
            // Top Selling Items
            yPosition = addTopSellingItems(contentStream, salesReport.getTopSellingItems(), yPosition);
            
            // Revenue by Category
            addRevenueByCategory(contentStream, salesReport.getRevenueByCategory(), yPosition);
        }
    }

    private float addTitle(PDPageContentStream contentStream, String title, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        return yPosition - (LEADING * 2);
    }

    private float addDateSection(PDPageContentStream contentStream, float yPosition) throws IOException {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm"));
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Generated: " + dateStr);
        contentStream.endText();
        return yPosition - (LEADING * 2);
    }

    private float addSalesOverview(PDPageContentStream contentStream, SalesReport report, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Sales Overview");
        contentStream.endText();
        yPosition -= LEADING * 1.5;

        String[] details = {
            String.format("Total Orders: %d", report.getTotalOrders()),
            String.format("Total Revenue: $%.2f", report.getTotalRevenue()),
            String.format("Average Order Value: $%.2f", 
                report.getTotalOrders() > 0 ? report.getTotalRevenue() / report.getTotalOrders() : 0)
        };

        for (String detail : details) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN + 20, yPosition);
            contentStream.showText(detail);
            contentStream.endText();
            yPosition -= LEADING;
        }

        return yPosition - LEADING;
    }

    private float addInventoryStatus(PDPageContentStream contentStream, Map<String, Integer> status, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Inventory Status");
        contentStream.endText();
        yPosition -= LEADING * 1.5;

        for (Map.Entry<String, Integer> entry : status.entrySet()) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN + 20, yPosition);
            contentStream.showText(String.format("%s: %d items", entry.getKey(), entry.getValue()));
            contentStream.endText();
            yPosition -= LEADING;
        }

        return yPosition - LEADING;
    }

    private float addEmployeeWorkSummary(PDPageContentStream contentStream, List<WorkLog> workLogs, List<User> employees, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Employee Work Summary");
        contentStream.endText();
        yPosition -= LEADING * 1.5;

        for (User employee : employees) {
            List<WorkLog> employeeLogs = workLogs.stream()
                .filter(log -> log.getUserId().equals(employee.getId()))
                .toList();

            long totalHours = employeeLogs.stream()
                .filter(log -> log.getDuration() != null)
                .mapToLong(log -> log.getDuration().toHours())
                .sum();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN + 20, yPosition);
            contentStream.showText(String.format("%s: %d hours", employee.getUsername(), totalHours));
            contentStream.endText();
            yPosition -= LEADING;
        }

        return yPosition - LEADING;
    }

    private float addTopSellingItems(PDPageContentStream contentStream, Map<String, Integer> topItems, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Top Selling Items");
        contentStream.endText();
        yPosition -= LEADING * 1.5;

        for (Map.Entry<String, Integer> entry : topItems.entrySet()) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN + 20, yPosition);
            contentStream.showText(String.format("%s: %d units", entry.getKey(), entry.getValue()));
            contentStream.endText();
            yPosition -= LEADING;
        }

        return yPosition - LEADING;
    }

    private void addRevenueByCategory(PDPageContentStream contentStream, Map<String, Double> revenue, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Revenue by Category");
        contentStream.endText();
        yPosition -= LEADING * 1.5;

        for (Map.Entry<String, Double> entry : revenue.entrySet()) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
            contentStream.newLineAtOffset(MARGIN + 20, yPosition);
            contentStream.showText(String.format("%s: $%.2f", entry.getKey(), entry.getValue()));
            contentStream.endText();
            yPosition -= LEADING;
        }
    }
}
