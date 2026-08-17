package dev.byankit.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final String DEFAULT_PROPERTIES_FILE = "app.properties";
    private final Properties properties = new Properties();

    public AppConfig() {
        loadProperties(null);
    }

    public AppConfig(String customConfigPath) {
        loadProperties(customConfigPath);
    }

    private void loadProperties(String customConfigPath) {
        // 1. Try custom file path if specified
        if (customConfigPath != null && !customConfigPath.isBlank()) {
            File customFile = new File(customConfigPath);
            if (customFile.exists()) {
                try (InputStream input = new FileInputStream(customFile)) {
                    properties.load(input);
                    return;
                } catch (IOException e) {
                    System.err.println("Warning: Could not load custom config from " + customConfigPath + ": " + e.getMessage());
                }
            }
        }

        // 2. Try classpath resource 'app.properties'
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load classpath app.properties: " + e.getMessage());
        }
    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        String val = properties.getProperty(key);
        if (val != null) {
            try {
                return Integer.parseInt(val.trim());
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        String val = properties.getProperty(key);
        if (val != null) {
            try {
                return Double.parseDouble(val.trim());
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String val = properties.getProperty(key);
        if (val != null) {
            return Boolean.parseBoolean(val.trim());
        }
        return defaultValue;
    }

    // Convenience getters for generator defaults
    public String getDefaultSource() {
        return getString("generator.default.source", "HDFC");
    }

    public String getDefaultType() {
        return getString("generator.default.type", "BANKSTATEMENT");
    }

    public int getDefaultCount() {
        return getInt("generator.default.count", 50);
    }

    public double getDefaultInitialBalance() {
        return getDouble("generator.default.initial_balance", 50000.00);
    }

    public String getDefaultOutputPath() {
        return getString("generator.default.output_path", "hdfc_statement.csv");
    }

    public String getDefaultDateFormat() {
        return getString("generator.default.date_format", "dd/MM/yy");
    }
}
