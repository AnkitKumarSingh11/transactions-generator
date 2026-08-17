package dev.byankit.generator;

import dev.byankit.config.AppConfig;
import dev.byankit.model.PersonProfile;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;

import java.util.List;

public class HdfcStatementGenerator implements StatementGenerator {
    private final DataRandomizer randomizer;
    private final AppConfig appConfig;
    private final SyntheaFinancialEngine syntheaEngine;

    public HdfcStatementGenerator() {
        this.randomizer = new DataRandomizer();
        this.appConfig = new AppConfig();
        this.syntheaEngine = new SyntheaFinancialEngine(randomizer, appConfig);
    }

    public HdfcStatementGenerator(DataRandomizer randomizer) {
        this.randomizer = randomizer != null ? randomizer : new DataRandomizer();
        this.appConfig = new AppConfig();
        this.syntheaEngine = new SyntheaFinancialEngine(this.randomizer, this.appConfig);
    }

    public HdfcStatementGenerator(DataRandomizer randomizer, AppConfig appConfig) {
        this.randomizer = randomizer != null ? randomizer : new DataRandomizer();
        this.appConfig = appConfig != null ? appConfig : new AppConfig();
        this.syntheaEngine = new SyntheaFinancialEngine(this.randomizer, this.appConfig);
    }

    @Override
    public List<Transaction> generate(StatementConfig config) {
        return syntheaEngine.simulateFinancialLifecycle(config);
    }

    public PersonProfile getLastGeneratedProfile() {
        return syntheaEngine.getLastGeneratedProfile();
    }
}
