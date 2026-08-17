package dev.byankit.generator;

import dev.byankit.enums.TransactionSource;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HdfcStatementGeneratorTest {

    private HdfcStatementGenerator generator;

    @BeforeEach
    void setUp() {
        DataRandomizer randomizer = new DataRandomizer(42L);
        generator = new HdfcStatementGenerator(randomizer);
    }

    @Test
    void testGenerateTransactionsCountAndChronology() {
        int count = 25;
        double initialBalance = 100000.00;
        LocalDate startDate = LocalDate.of(2023, 5, 1);
        LocalDate endDate = LocalDate.of(2023, 5, 31);

        StatementConfig config = StatementConfig.builder()
                .source(TransactionSource.HDFC)
                .count(count)
                .initialBalance(initialBalance)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        List<Transaction> transactions = generator.generate(config);

        assertEquals(count, transactions.size());

        double prevBalance = initialBalance;
        LocalDate prevDate = startDate;

        for (Transaction tx : transactions) {
            // Assert date chronology
            assertFalse(tx.getDate().isBefore(prevDate));
            prevDate = tx.getDate();

            // Assert balance math
            double debit = tx.getDebitAmount() != null ? tx.getDebitAmount() : 0.0;
            double credit = tx.getCreditAmount() != null ? tx.getCreditAmount() : 0.0;

            double expectedBalance = Math.round((prevBalance + credit - debit) * 100.0) / 100.0;
            assertEquals(expectedBalance, tx.getClosingBalance(), 0.01,
                    "Closing balance mismatch for transaction narration: " + tx.getNarration());

            prevBalance = tx.getClosingBalance();
            assertTrue(tx.getClosingBalance() >= 0, "Closing balance should not be negative");
        }
    }
}
