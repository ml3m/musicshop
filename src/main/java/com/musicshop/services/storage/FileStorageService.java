package com.musicshop.services.storage;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.musicshop.models.sales.Order;
import com.musicshop.models.music.MusicItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.musicshop.models.user.User;
import com.musicshop.models.user.WorkLog;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileStorageService {
    private static final String DATA_DIRECTORY = "src/main/data";
    private static final String INVENTORY_FILE_NAME = "inventory.json";
    private static final String ORDERS_FILE_NAME = "orders.json";
    private static final String USERS_FILE_NAME = "users.json";
    private static final String WORKLOGS_FILE_NAME = "worklogs.json";

    private static final Logger logger = Logger.getLogger(FileStorageService.class.getName());
    private final ObjectMapper objectMapper;

    public FileStorageService() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        logger.setLevel(Level.WARNING);
    }

    // helper method to load data from a JSON file into a list
    private <T> List<T> loadData(String fileName, TypeReference<List<T>> typeReference) {
        try {
            File file = getFilePath(fileName).toFile();
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading data from " + fileName, e);
            return new ArrayList<>();
        }
    }

    // helper method to save data to a JSON file
    private <T> void saveData(String fileName, List<T> data) {
        try {
            objectMapper.writeValue(getFilePath(fileName).toFile(), data);
            logger.info(fileName + " saved to JSON file.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error saving data to " + fileName, e);
        }
    }

    ////////////////////////
    // LOADERS AND SAVERS //
    ////////////////////////

    // Load orders from the orders JSON file
    public List<Order> loadOrders() {
        try {
            File file = getFilePath(ORDERS_FILE_NAME).toFile();
            if (file.exists()) {
                return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading orders", e);
        }
        return new ArrayList<>();
    }

    // helper method to get the file path
    private Path getFilePath(String fileName) { return Paths.get(DATA_DIRECTORY, fileName).toAbsolutePath(); }
    // Save orders to the orders JSON file
    public void saveOrders(List<Order> orders) { saveData(ORDERS_FILE_NAME, orders); }
    // Load work logs from the work logs JSON file
    public List<WorkLog> loadWorkLogs() { return loadData(WORKLOGS_FILE_NAME, new TypeReference<>() {}); }
    // Save work logs to the work logs JSON file
    public void saveWorkLogs(List<WorkLog> workLogs) {
        saveData(WORKLOGS_FILE_NAME, workLogs);
    }
    // Load users from the users JSON file
    public List<User> loadUsers() { return loadData(USERS_FILE_NAME, new TypeReference<>() { }); }
    // Save users to the users JSON file
    public void saveUsers(List<User> users) {
        saveData(USERS_FILE_NAME, users);
    }
    // Load items from the inventory JSON file
    public List<MusicItem> loadItems() { return loadData(INVENTORY_FILE_NAME, new TypeReference<>() { }); }
    // Save items to the inventory JSON file
    public void saveItems(List<MusicItem> items) {
        saveData(INVENTORY_FILE_NAME, items);
    }
}