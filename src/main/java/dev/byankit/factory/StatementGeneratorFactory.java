package dev.byankit.factory;

import dev.byankit.enums.TransactionSource;
import dev.byankit.generator.HdfcStatementGenerator;
import dev.byankit.generator.StatementGenerator;

public class StatementGeneratorFactory {

    public static StatementGenerator getGenerator(TransactionSource source) {
        if (source == null) {
            return new HdfcStatementGenerator();
        }
        switch (source) {
            case HDFC:
            case PAYZAPP:
            case SBI:
            case PAYTM:
            default:
                // Currently starting with HDFC Bank schema generation
                return new HdfcStatementGenerator();
        }
    }
}
