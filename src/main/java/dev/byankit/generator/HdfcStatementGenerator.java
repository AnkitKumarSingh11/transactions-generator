package dev.byankit.generator;

import dev.byankit.config.AppConfig;
import dev.byankit.schema.HdfcStatementSchema;

public class HdfcStatementGenerator extends GenericStatementGenerator {

    public HdfcStatementGenerator() {
        super(new HdfcStatementSchema());
    }

    public HdfcStatementGenerator(DataRandomizer randomizer) {
        super(new HdfcStatementSchema(), randomizer, new AppConfig());
    }

    public HdfcStatementGenerator(DataRandomizer randomizer, AppConfig appConfig) {
        super(new HdfcStatementSchema(), randomizer, appConfig);
    }
}
