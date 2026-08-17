package dev.byankit.schema;

import dev.byankit.enums.TransactionSource;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;

import java.util.List;

public interface StatementSchema {
    /**
     * @return The transaction source/bank enum associated with this schema.
     */
    TransactionSource getSource();

    /**
     * @return The exact CSV header column names for this bank/wallet statement format.
     */
    List<String> getHeaders();

    /**
     * Formats a domain Transaction object into a list of row string values matching this schema's columns.
     *
     * @param tx The transaction domain object
     * @param config The statement configuration
     * @return List of CSV string cell values
     */
    List<String> formatRecord(Transaction tx, StatementConfig config);

    /**
     * @return The default date format pattern used by this bank/wallet schema.
     */
    String getDefaultDateFormat();
}
