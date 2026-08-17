package dev.byankit.generator;

import dev.byankit.enums.TransactionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DataRandomizerTest {

    private DataRandomizer randomizer;

    @BeforeEach
    void setUp() {
        randomizer = new DataRandomizer(12345L);
    }

    @Test
    void testGeneratePersonName() {
        String name = randomizer.generatePersonName();
        assertNotNull(name);
        assertFalse(name.isBlank());
        assertEquals(name, name.toUpperCase());
    }

    @Test
    void testGenerateUtrNumber() {
        String utr = randomizer.generateUtrNumber();
        assertNotNull(utr);
        assertEquals(12, utr.length());
        assertTrue(utr.matches("\\d{12}"));
    }

    @Test
    void testGenerateNeftRefNumber() {
        String neftRef = randomizer.generateNeftRefNumber();
        assertNotNull(neftRef);
        assertTrue(neftRef.startsWith("N"));
        assertEquals(12, neftRef.length());
    }

    @Test
    void testGenerateAmount() {
        double amount = randomizer.generateAmount(TransactionCategory.UPI, true);
        assertTrue(amount > 0);
    }

    @Test
    void testGenerateRandomDateBetween() {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);
        LocalDate randomDate = randomizer.generateRandomDateBetween(start, end);
        assertNotNull(randomDate);
        assertFalse(randomDate.isBefore(start));
        assertFalse(randomDate.isAfter(end));
    }
}
