package dev.byankit.factory;

import dev.byankit.enums.TransactionSource;
import dev.byankit.generator.GenericStatementGenerator;
import dev.byankit.generator.HdfcStatementGenerator;
import dev.byankit.generator.StatementGenerator;
import dev.byankit.schema.SchemaRegistry;
import dev.byankit.schema.StatementSchema;

public class StatementGeneratorFactory {

    public static StatementGenerator getGenerator(TransactionSource source) {
        if (source == null) {
            return new HdfcStatementGenerator();
        }
        StatementSchema schema = SchemaRegistry.getSchema(source);
        return new GenericStatementGenerator(schema);
    }
}
