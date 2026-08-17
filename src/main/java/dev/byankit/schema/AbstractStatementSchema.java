package dev.byankit.schema;

import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class AbstractStatementSchema implements StatementSchema {

    protected String formatDate(LocalDate date, StatementConfig config) {
        if (date == null) {
            return "";
        }
        String pattern = (config != null && config.getDateFormat() != null && !config.getDateFormat().isBlank())
                ? config.getDateFormat()
                : getDefaultDateFormat();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return date.format(formatter);
    }

    protected String formatAmount(Double amount) {
        if (amount == null || amount <= 0) {
            return "";
        }
        return String.format(Locale.ENGLISH, "%.2f", amount);
    }

    protected String formatBalance(Double balance) {
        if (balance == null) {
            return "0.00";
        }
        return String.format(Locale.ENGLISH, "%.2f", balance);
    }
}
