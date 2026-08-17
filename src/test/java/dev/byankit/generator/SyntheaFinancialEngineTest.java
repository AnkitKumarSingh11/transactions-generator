package dev.byankit.generator;

import dev.byankit.config.AppConfig;
import dev.byankit.enums.TransactionCategory;
import dev.byankit.enums.TransactionSource;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SyntheaFinancialEngineTest {

    private SyntheaFinancialEngine engine;

    @BeforeEach
    void setUp() {
        DataRandomizer randomizer = new DataRandomizer(99L);
        AppConfig appConfig = new AppConfig();
        engine = new SyntheaFinancialEngine(randomizer, appConfig);
    }

    @Test
    void testSimulateFinancialLifecycleEvents() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 31); // 3 months

        StatementConfig config = StatementConfig.builder()
                .source(TransactionSource.HDFC)
                .count(30)
                .initialBalance(60000.00)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<Transaction> transactions = engine.simulateFinancialLifecycle(config);

        assertEquals(30, transactions.size());

        boolean hasSalary = false;
        boolean hasRent = false;

        for (Transaction tx : transactions) {
            if (tx.getCategory() == TransactionCategory.SALARY) {
                hasSalary = true;
            }
            if (tx.getCategory() == TransactionCategory.NEFT && tx.getNarration().contains("RENT")) {
                hasRent = true;
            }
        }

        assertTrue(hasSalary, "Should generate synthetic monthly salary event");
        assertTrue(hasRent, "Should generate synthetic monthly rent event");
    }
}
