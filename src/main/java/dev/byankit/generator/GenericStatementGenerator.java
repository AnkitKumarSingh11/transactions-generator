package dev.byankit.generator;

import dev.byankit.config.AppConfig;
import dev.byankit.model.PersonProfile;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;
import dev.byankit.schema.SchemaRegistry;
import dev.byankit.schema.StatementSchema;

import java.util.List;

public class GenericStatementGenerator implements StatementGenerator {
    private final StatementSchema schema;
    private final DataRandomizer randomizer;
    private final AppConfig appConfig;
    private final SyntheaFinancialEngine syntheaEngine;

    public GenericStatementGenerator(StatementSchema schema) {
        this(schema, new DataRandomizer(), new AppConfig());
    }

    public GenericStatementGenerator(StatementSchema schema, DataRandomizer randomizer, AppConfig appConfig) {
        this.schema = schema != null ? schema : SchemaRegistry.getSchema(null);
        this.randomizer = randomizer != null ? randomizer : new DataRandomizer();
        this.appConfig = appConfig != null ? appConfig : new AppConfig();
        this.syntheaEngine = new SyntheaFinancialEngine(this.randomizer, this.appConfig);
    }

    @Override
    public List<Transaction> generate(StatementConfig config) {
        return syntheaEngine.simulateFinancialLifecycle(config);
    }

    public StatementSchema getSchema() {
        return schema;
    }

    public PersonProfile getLastGeneratedProfile() {
        return syntheaEngine.getLastGeneratedProfile();
    }
}
