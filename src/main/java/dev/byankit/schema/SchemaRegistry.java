package dev.byankit.schema;

import dev.byankit.enums.TransactionSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchemaRegistry {
    private static final Map<TransactionSource, StatementSchema> REGISTRY = new ConcurrentHashMap<>();

    static {
        register(new HdfcStatementSchema());
        register(new PaytmStatementSchema());
    }

    public static void register(StatementSchema schema) {
        if (schema != null && schema.getSource() != null) {
            REGISTRY.put(schema.getSource(), schema);
        }
    }

    public static StatementSchema getSchema(TransactionSource source) {
        if (source == null) {
            return REGISTRY.get(TransactionSource.HDFC);
        }
        StatementSchema schema = REGISTRY.get(source);
        if (schema == null) {
            return REGISTRY.get(TransactionSource.HDFC);
        }
        return schema;
    }
}
