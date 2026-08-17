package dev.byankit.writer;

import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;
import dev.byankit.schema.HdfcStatementSchema;
import dev.byankit.schema.SchemaRegistry;
import dev.byankit.schema.StatementSchema;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CsvStatementWriter {

    /**
     * Writes transactions to CSV using default schema resolved from config.getSource().
     */
    public void writeToFile(List<Transaction> transactions, StatementConfig config) throws IOException {
        StatementSchema schema = SchemaRegistry.getSchema(config.getSource());
        writeToFile(transactions, config, schema);
    }

    /**
     * Writes transactions to CSV using the specified StatementSchema.
     *
     * @param transactions List of domain transactions
     * @param config Statement configuration
     * @param schema Bank/Wallet StatementSchema
     * @throws IOException If writing fails
     */
    public void writeToFile(List<Transaction> transactions, StatementConfig config, StatementSchema schema) throws IOException {
        if (schema == null) {
            schema = new HdfcStatementSchema();
        }

        File file = new File(config.getOutputPath());
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        List<String> headers = schema.getHeaders();
        String[] headerArray = headers.toArray(new String[0]);

        try (Writer writer = new BufferedWriter(new FileWriter(file));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader(headerArray)
                     .build())) {

            for (Transaction tx : transactions) {
                List<String> record = schema.formatRecord(tx, config);
                csvPrinter.printRecord(record);
            }
            csvPrinter.flush();
        }
    }
}
