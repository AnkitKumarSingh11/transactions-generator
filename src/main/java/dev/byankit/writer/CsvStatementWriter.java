package dev.byankit.writer;

import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class CsvStatementWriter {
    public static final String[] HDFC_DEFAULT_HEADERS = {
            "Date", "Narration Value Dat", "Debit Amount", "Credit Amount", "Chq/Ref Number", "Closing Balance"
    };

    /**
     * Writes the given list of transactions to a CSV file.
     *
     * @param transactions List of transactions to write
     * @param config Statement configuration
     * @throws IOException If file creation or writing fails
     */
    public void writeToFile(List<Transaction> transactions, StatementConfig config) throws IOException {
        File file = new File(config.getOutputPath());
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (Writer writer = new BufferedWriter(new FileWriter(file));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader(HDFC_DEFAULT_HEADERS)
                     .build())) {

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(config.getDateFormat(), Locale.ENGLISH);

            for (Transaction tx : transactions) {
                String formattedDate = tx.getDate() != null ? tx.getDate().format(dateFormatter) : "";
                String narration = tx.getNarration() != null ? tx.getNarration() : "";
                String debit = (tx.getDebitAmount() != null && tx.getDebitAmount() > 0)
                        ? String.format(Locale.ENGLISH, "%.2f", tx.getDebitAmount())
                        : "";
                String credit = (tx.getCreditAmount() != null && tx.getCreditAmount() > 0)
                        ? String.format(Locale.ENGLISH, "%.2f", tx.getCreditAmount())
                        : "";
                String refNum = tx.getRefNumber() != null ? tx.getRefNumber() : "";
                String closingBal = tx.getClosingBalance() != null
                        ? String.format(Locale.ENGLISH, "%.2f", tx.getClosingBalance())
                        : "0.00";

                csvPrinter.printRecord(formattedDate, narration, debit, credit, refNum, closingBal);
            }
            csvPrinter.flush();
        }
    }
}
