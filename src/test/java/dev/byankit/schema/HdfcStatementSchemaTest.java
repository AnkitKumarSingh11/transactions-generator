package dev.byankit.schema;

import dev.byankit.enums.TransactionCategory;
import dev.byankit.enums.TransactionSource;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HdfcStatementSchemaTest {

    @Test
    void testHdfcStatementSchemaHeadersAndFormatting() {
        StatementSchema schema = new HdfcStatementSchema();
        assertEquals(TransactionSource.HDFC, schema.getSource());

        List<String> headers = schema.getHeaders();
        assertEquals(6, headers.size());
        assertEquals("Date", headers.get(0));
        assertEquals("Narration Value Dat", headers.get(1));
        assertEquals("Debit Amount", headers.get(2));
        assertEquals("Credit Amount", headers.get(3));
        assertEquals("Chq/Ref Number", headers.get(4));
        assertEquals("Closing Balance", headers.get(5));

        Transaction tx = new Transaction(
                LocalDate.of(2023, 8, 15),
                LocalDate.of(2023, 8, 15),
                "UPI-123456789012-SWIGGY",
                250.50,
                null,
                "123456789012",
                49749.50,
                TransactionCategory.UPI
        );

        StatementConfig config = StatementConfig.builder().dateFormat("dd/MM/yy").build();
        List<String> record = schema.formatRecord(tx, config);

        assertEquals(6, record.size());
        assertEquals("15/08/23", record.get(0));
        assertEquals("UPI-123456789012-SWIGGY", record.get(1));
        assertEquals("250.50", record.get(2));
        assertEquals("", record.get(3));
        assertEquals("123456789012", record.get(4));
        assertEquals("49749.50", record.get(5));
    }
}
