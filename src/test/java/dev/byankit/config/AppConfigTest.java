package dev.byankit.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void testDefaultAppPropertiesLoading() {
        AppConfig config = new AppConfig();
        assertEquals("HDFC", config.getDefaultSource());
        assertEquals("BANKSTATEMENT", config.getDefaultType());
        assertEquals(50, config.getDefaultCount());
        assertEquals(50000.00, config.getDefaultInitialBalance());
        assertEquals("hdfc_statement.csv", config.getDefaultOutputPath());
        assertEquals("dd/MM/yy", config.getDefaultDateFormat());
    }
}
