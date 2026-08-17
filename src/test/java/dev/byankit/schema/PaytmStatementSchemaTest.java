package dev.byankit.schema;

import dev.byankit.enums.TransactionCategory;
import dev.byankit.enums.TransactionSource;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaytmStatementSchemaTest {

    @Test
    void testPaytmStatementSchemaHeadersAndFormatting() {
        StatementSchema schema = new PaytmStatementSchema();
        assertEquals(TransactionSource.PAYTM, schema.getSource());

        List<String> headers = schema.getHeaders();
        assertEquals(11, headers.size());
        assertEquals("Date", headers.get(0));
        assertEquals("Time", headers.get(1));
        assertEquals("Transaction Details", headers.get(2));
        assertEquals("Other Transaction Details (UPI ID or A/c No)", headers.get(3));
        assertEquals("Your Account", headers.get(4));
        assertEquals("Amount", headers.get(5));
        assertEquals("UPI Ref No.", headers.get(6));
        assertEquals("Order ID", headers.get(7));
        assertEquals("Remarks", headers.get(8));
        assertEquals("Tags", headers.get(9));
        assertEquals("Comment", headers.get(10));

        Transaction tx = new Transaction(
                LocalDate.of(2026, 8, 17),
                LocalDate.of(2026, 8, 17),
                "UPI-622942725203-Amina-HDFC-amina@okicici",
                114.00,
                null,
                "622942725203",
                4886.00,
                TransactionCategory.UPI
        );

        StatementConfig config = StatementConfig.builder()
                .dateFormat("dd/MM/yyyy")
                .paytmId("paytm.s1mdx1j@pty")
                .build();

        List<String> record = schema.formatRecord(tx, config);

        assertEquals(11, record.size());
        assertEquals("17/08/2026", record.get(0)); // Date
        assertTrue(record.get(1).matches("\\d{2}:\\d{2}:\\d{2}")); // Time
        assertTrue(record.get(2).contains("Paid to Amina")); // Transaction Details
        assertEquals("paytm.s1mdx1j@pty on Paytm", record.get(3)); // Other Details
        assertEquals("-114.00", record.get(5)); // Amount (signed)
        assertEquals("622942725203", record.get(6)); // UPI Ref No.
    }
}
