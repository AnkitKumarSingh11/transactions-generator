package dev.byankit.generator;

import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;
import java.util.List;

public interface StatementGenerator {
    /**
     * Generates a list of transactions according to the provided config.
     *
     * @param config Statement configuration including date ranges, count, initial balance, etc.
     * @return List of generated transactions in chronological order.
     */
    List<Transaction> generate(StatementConfig config);
}
