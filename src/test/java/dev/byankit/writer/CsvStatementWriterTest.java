package dev.byankit.writer;

import dev.byankit.enums.TransactionCategory;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvStatementWriterTest {

    @Test
    void testWriteToFileHeadersAndContent() throws Exception {
        File tempFile = File.createTempFile("test_hdfc_statement", ".csv");
        tempFile.deleteOnExit();

        Transaction tx1 = new Transaction(
                LocalDate.of(2023, 8, 15),
                LocalDate.of(2023, 8, 15),
                "UPI-123456789012-SWIGGY-HDFC-swiggy@okicici",
                250.50,
                null,
                "123456789012",
                49749.50,
                TransactionCategory.UPI
        );

        Transaction tx2 = new Transaction(
                LocalDate.of(2023, 8, 16),
                LocalDate.of(2023, 8, 16),
                "ACH C- ACME CORP SALARY 987654",
                null,
                75000.00,
                "987654321098",
                124749.50,
                TransactionCategory.SALARY
        );

        StatementConfig config = StatementConfig.builder()
                .outputPath(tempFile.getAbsolutePath())
                .dateFormat("dd/MM/yy")
                .build();

        CsvStatementWriter writer = new CsvStatementWriter();
        writer.writeToFile(List.of(tx1, tx2), config);

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);

        try (Reader in = new FileReader(tempFile);
             CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(in)) {

            List<CSVRecord> records = parser.getRecords();
            assertEquals(2, records.size());

            CSVRecord r1 = records.get(0);
            assertEquals("15/08/23", r1.get("Date"));
            assertEquals("UPI-123456789012-SWIGGY-HDFC-swiggy@okicici", r1.get("Narration Value Dat"));
            assertEquals("250.50", r1.get("Debit Amount"));
            assertEquals("", r1.get("Credit Amount"));
            assertEquals("123456789012", r1.get("Chq/Ref Number"));
            assertEquals("49749.50", r1.get("Closing Balance"));

            CSVRecord r2 = records.get(1);
            assertEquals("16/08/23", r2.get("Date"));
            assertEquals("ACH C- ACME CORP SALARY 987654", r2.get("Narration Value Dat"));
            assertEquals("", r2.get("Debit Amount"));
            assertEquals("75000.00", r2.get("Credit Amount"));
            assertEquals("987654321098", r2.get("Chq/Ref Number"));
            assertEquals("124749.50", r2.get("Closing Balance"));
        }
    }
}
