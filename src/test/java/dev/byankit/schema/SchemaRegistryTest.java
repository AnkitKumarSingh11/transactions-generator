package dev.byankit.schema;

import dev.byankit.enums.TransactionSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchemaRegistryTest {

    @Test
    void testGetHdfcSchema() {
        StatementSchema schema = SchemaRegistry.getSchema(TransactionSource.HDFC);
        assertNotNull(schema);
        assertEquals(TransactionSource.HDFC, schema.getSource());
        assertNotNull(schema.getHeaders());
        assertFalse(schema.getHeaders().isEmpty());
    }

    @Test
    void testFallbackToHdfcSchemaWhenUnregistered() {
        // Unregistered sources default to active HDFC schema until registered
        StatementSchema schema = SchemaRegistry.getSchema(TransactionSource.SBI);
        assertNotNull(schema);
        assertEquals(TransactionSource.HDFC, schema.getSource());
    }
}
