package com.musicshop;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                // Handle error or use defaults
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getStartMode() {
        return properties.getProperty("startMode", "production"); // Default to "production" if not set
    }
    public static boolean getShowTest() {
        String viewInventory = properties.getProperty("viewInventory", "false"); // Default to "none" if not set
        return Boolean.parseBoolean(viewInventory); // Parse as boolean
    }
    public static boolean getViewOrders() {
        String viewOrders = properties.getProperty("viewOrders", "false"); // Default to "false" if not set
        return Boolean.parseBoolean(viewOrders); // Parse as boolean
    }
    public static boolean getAddInventoryItem() {
        String addInventoryItem = properties.getProperty("addInventoryItem", "false"); // Default to "false" if not set
        return Boolean.parseBoolean(addInventoryItem); // Parse as boolean
    }
    public static boolean getConfigAllow() {
        String configAllow = properties.getProperty("allowConfig", "false"); // Default to "false" if not set
        return Boolean.parseBoolean(configAllow); // Parse as boolean
    }
}
