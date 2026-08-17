package dev.byankit.cli;

import dev.byankit.config.AppConfig;
import dev.byankit.enums.TransactionSource;
import dev.byankit.enums.TransactionType;
import dev.byankit.factory.StatementGeneratorFactory;
import dev.byankit.generator.GenericStatementGenerator;
import dev.byankit.generator.StatementGenerator;
import dev.byankit.model.PersonProfile;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;
import dev.byankit.schema.SchemaRegistry;
import dev.byankit.schema.StatementSchema;
import dev.byankit.writer.CsvStatementWriter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "generate",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "Universal synthetic financial transaction statement generator (HDFC, Paytm)."
)
public class GenerateCommand implements Callable<Integer> {

    @Option(
            names = {"-c", "--config"},
            description = "Path to custom app.properties file."
    )
    private String configPath;

    @Option(
            names = {"-s", "--source"},
            description = "Transaction source/bank schema (HDFC, PAYTM, ALL). Default: HDFC",
            defaultValue = "HDFC"
    )
    private String sourceName;

    @Option(
            names = {"-t", "--type"},
            description = "Transaction type (BANKSTATEMENT, UPISERVICE, WALLET)."
    )
    private String typeName;

    @Option(
            names = {"-n", "--count"},
            description = "Number of transactions to generate per statement."
    )
    private Integer count;

    @Option(
            names = {"-b", "--initial-balance"},
            description = "Starting account balance."
    )
    private Double initialBalance;

    @Option(
            names = {"-o", "--output"},
            description = "Output CSV file path (Default: auto-generated based on Name & Account/Paytm ID)."
    )
    private String outputPath;

    @Option(
            names = {"--name", "--account-holder"},
            description = "Custom account holder name (e.g. 'Ankit Singh'). Default: Randomly generated"
    )
    private String accountHolderName;

    @Option(
            names = {"--account-number", "--account-no"},
            description = "Custom account number (e.g. '50100987654321'). Default: Randomly generated"
    )
    private String accountNumber;

    @Option(
            names = {"--paytm-id"},
            description = "Custom Paytm ID / VPA (e.g. 'paytm.s1mdx1j@pty'). Default: Randomly generated"
    )
    private String paytmId;

    @Option(
            names = {"--start-date"},
            description = "Start date (YYYY-MM-DD). Default: 30 days ago",
            defaultValue = ""
    )
    private String startDateStr;

    @Option(
            names = {"--end-date"},
            description = "End date (YYYY-MM-DD). Default: today",
            defaultValue = ""
    )
    private String endDateStr;

    @Option(
            names = {"--date-format"},
            description = "Date format in output CSV."
    )
    private String dateFormat;

    @Override
    public Integer call() {
        try {
            AppConfig appConfig = new AppConfig(configPath);

            String finalTypeStr = (typeName != null && !typeName.isBlank()) ? typeName : appConfig.getDefaultType();
            int finalCount = (count != null && count > 0) ? count : appConfig.getDefaultCount();
            double finalBalance = (initialBalance != null && initialBalance >= 0) ? initialBalance : appConfig.getDefaultInitialBalance();

            TransactionType type = TransactionType.fromString(finalTypeStr);

            LocalDate startDate = (startDateStr != null && !startDateStr.isBlank())
                    ? LocalDate.parse(startDateStr)
                    : LocalDate.now().minusDays(30);

            LocalDate endDate = (endDateStr != null && !endDateStr.isBlank())
                    ? LocalDate.parse(endDateStr)
                    : LocalDate.now();

            List<TransactionSource> sourcesToGenerate = new ArrayList<>();
            if ("ALL".equalsIgnoreCase(sourceName.trim())) {
                sourcesToGenerate.add(TransactionSource.HDFC);
                sourcesToGenerate.add(TransactionSource.PAYTM);
            } else {
                sourcesToGenerate.add(TransactionSource.fromString(sourceName));
            }

            for (TransactionSource source : sourcesToGenerate) {
                StatementSchema schema = SchemaRegistry.getSchema(source);

                String finalDateFormat = (dateFormat != null && !dateFormat.isBlank())
                        ? dateFormat
                        : schema.getDefaultDateFormat();

                StatementConfig config = StatementConfig.builder()
                        .source(source)
                        .type(type)
                        .count(finalCount)
                        .initialBalance(finalBalance)
                        .startDate(startDate)
                        .endDate(endDate)
                        .outputPath(outputPath)
                        .dateFormat(finalDateFormat)
                        .accountHolderName(accountHolderName)
                        .accountNumber(accountNumber)
                        .paytmId(paytmId)
                        .build();

                StatementGenerator generator = StatementGeneratorFactory.getGenerator(source);
                List<Transaction> transactions = generator.generate(config);

                PersonProfile profile = null;
                if (generator instanceof GenericStatementGenerator) {
                    profile = ((GenericStatementGenerator) generator).getLastGeneratedProfile();
                }

                // Determine output file path according to naming rules
                String finalOutput = outputPath;
                if (finalOutput == null || finalOutput.isBlank() || sourcesToGenerate.size() > 1) {
                    if (profile != null) {
                        String cleanName = profile.getFullName().replaceAll("[^a-zA-Z0-9]", "_");
                        if (source == TransactionSource.PAYTM) {
                            String cleanPaytmId = profile.getPaytmId().replaceAll("[^a-zA-Z0-9._-]", "_");
                            finalOutput = String.format("%s_%s_PAYTM_statement.csv", cleanName, cleanPaytmId);
                        } else {
                            finalOutput = String.format("%s_%s_%s_statement.csv", cleanName, profile.getAccountNumber(), source.name());
                        }
                    } else {
                        finalOutput = source.name().toLowerCase() + "_statement.csv";
                    }
                }

                StatementConfig updatedConfig = StatementConfig.builder()
                        .source(source)
                        .type(type)
                        .count(finalCount)
                        .initialBalance(finalBalance)
                        .startDate(startDate)
                        .endDate(endDate)
                        .outputPath(finalOutput)
                        .dateFormat(finalDateFormat)
                        .accountHolderName(accountHolderName)
                        .accountNumber(accountNumber)
                        .paytmId(paytmId)
                        .build();

                CsvStatementWriter writer = new CsvStatementWriter();
                writer.writeToFile(transactions, updatedConfig, schema);

                File outputFile = new File(finalOutput);
                double endBalance = transactions.isEmpty() ? finalBalance : transactions.get(transactions.size() - 1).getClosingBalance();

                System.out.println("=================================================");
                System.out.println(" FinStream Statement Generator");
                System.out.println("=================================================");
                System.out.printf(" Account Holder  : %s%n", profile != null ? profile.getFullName() : (accountHolderName != null ? accountHolderName : "N/A"));
                if (source == TransactionSource.PAYTM) {
                    System.out.printf(" Paytm VPA / ID  : %s%n", profile != null ? profile.getPaytmId() : (paytmId != null ? paytmId : "N/A"));
                } else {
                    System.out.printf(" Account Number  : %s%n", profile != null ? profile.getAccountNumber() : (accountNumber != null ? accountNumber : "N/A"));
                }
                System.out.printf(" Schema / Source : %s (%s)%n", source.getName(), source.name());
                System.out.printf(" Column Headers  : %s%n", String.join(", ", schema.getHeaders()));
                System.out.printf(" Record Count    : %d%n", finalCount);
                System.out.printf(" Start Balance   : ₹%.2f%n", finalBalance);
                System.out.printf(" Date Range      : %s to %s%n", startDate, endDate);
                System.out.printf(" Output File     : %s%n", finalOutput);
                System.out.println("-------------------------------------------------");
                System.out.println(" Status          : SUCCESS");
                System.out.printf(" Ending Balance  : ₹%.2f%n", endBalance);
                System.out.printf(" File Size       : %d bytes%n", outputFile.length());
                System.out.printf(" Absolute Path   : %s%n", outputFile.getAbsolutePath());
                System.out.println("=================================================");
            }

            return 0;
        } catch (Exception e) {
            System.err.println("Error generating transactions: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }
}
