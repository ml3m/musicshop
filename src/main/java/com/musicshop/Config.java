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
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        }

    // values are by default strings.
    public static String getStartMode() {
        return properties.getProperty("startMode", "production");
    }
    public static boolean getShowTest() {
        String viewInventory = properties.getProperty("viewInventory", "false");
        return Boolean.parseBoolean(viewInventory);
    }
    public static boolean getViewOrders() {
        String viewOrders = properties.getProperty("viewOrders", "false");
        return Boolean.parseBoolean(viewOrders);
    }
    public static boolean getAddInventoryItem() {
        String addInventoryItem = properties.getProperty("addInventoryItem", "false");
        return Boolean.parseBoolean(addInventoryItem);
    }
    public static boolean getConfigAllow() {
        String configAllow = properties.getProperty("allowConfig", "false");
        return Boolean.parseBoolean(configAllow);
    }
}
