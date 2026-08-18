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
import dev.byankit.writer.CsvUserWriter;
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
        description = "Universal synthetic financial bank transaction statement generator (HDFC, SBI)."
)
public class GenerateCommand implements Callable<Integer> {

    @Option(
            names = {"-c", "--config"},
            description = "Path to custom app.properties file."
    )
    private String configPath;

    @Option(
            names = {"-s", "--source"},
            description = "Transaction source/bank schema (HDFC, SBI, ALL). Default: HDFC",
            defaultValue = "HDFC"
    )
    private String sourceName;

    @Option(
            names = {"-t", "--type"},
            description = "Transaction type (BANKSTATEMENT, UPISERVICE)."
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
            description = "Output directory or specific file path (Default: current directory with auto-generated filename)."
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
            names = {"--start-date"},
            description = "Start date (YYYY-MM-DD). Default: 5 years ago",
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
                    : LocalDate.now().minusYears(5);

            LocalDate endDate = (endDateStr != null && !endDateStr.isBlank())
                    ? LocalDate.parse(endDateStr)
                    : LocalDate.now();

            List<TransactionSource> sourcesToGenerate = new ArrayList<>();
            if ("ALL".equalsIgnoreCase(sourceName.trim())) {
                sourcesToGenerate.add(TransactionSource.HDFC);
                sourcesToGenerate.add(TransactionSource.SBI);
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
                        .dateFormat(finalDateFormat)
                        .accountHolderName(accountHolderName)
                        .accountNumber(accountNumber)
                        .build();

                StatementGenerator generator = StatementGeneratorFactory.getGenerator(source);
                List<Transaction> transactions = generator.generate(config);

                PersonProfile profile = null;
                if (generator instanceof GenericStatementGenerator) {
                    profile = ((GenericStatementGenerator) generator).getLastGeneratedProfile();
                }

                // Auto-generate standard filenames based on profile & schema
                String autoFileName;
                String autoUserFileName;
                if (profile != null) {
                    String cleanName = profile.getFullName().replaceAll("[^a-zA-Z0-9]", "_");
                    autoFileName = String.format("%s_%s_%s_statement.csv", cleanName, profile.getAccountNumber(), source.name());
                    autoUserFileName = String.format("%s_%s_user_details.csv", cleanName, profile.getAccountNumber());
                } else {
                    autoFileName = source.name().toLowerCase() + "_statement.csv";
                    autoUserFileName = source.name().toLowerCase() + "_user_details.csv";
                }

                // Resolve output target files (handles directory input vs explicit file path input)
                File targetFile = resolveOutputFile(outputPath, autoFileName, sourcesToGenerate.size() > 1);
                File targetUserFile = resolveUserOutputFile(outputPath, targetFile, autoUserFileName, sourcesToGenerate.size() > 1);

                StatementConfig updatedConfig = StatementConfig.builder()
                        .source(source)
                        .type(type)
                        .count(finalCount)
                        .initialBalance(finalBalance)
                        .startDate(startDate)
                        .endDate(endDate)
                        .outputPath(targetFile.getAbsolutePath())
                        .dateFormat(finalDateFormat)
                        .accountHolderName(accountHolderName)
                        .accountNumber(accountNumber)
                        .build();

                CsvStatementWriter writer = new CsvStatementWriter();
                writer.writeToFile(transactions, updatedConfig, schema);

                if (profile != null) {
                    CsvUserWriter userWriter = new CsvUserWriter();
                    userWriter.writeToFile(profile, targetUserFile);
                }

                double endBalance = transactions.isEmpty() ? finalBalance : transactions.get(transactions.size() - 1).getClosingBalance();

                System.out.println("=================================================");
                System.out.println(" FinStream Statement Generator");
                System.out.println("=================================================");
                System.out.printf(" Account Holder  : %s%n", profile != null ? profile.getFullName() : (accountHolderName != null ? accountHolderName : "N/A"));
                System.out.printf(" Account Number  : %s%n", profile != null ? profile.getAccountNumber() : (accountNumber != null ? accountNumber : "N/A"));
                System.out.printf(" Schema / Source : %s (%s)%n", source.getName(), source.name());
                System.out.printf(" Column Headers  : %s%n", String.join(", ", schema.getHeaders()));
                System.out.printf(" Record Count    : %d%n", finalCount);
                System.out.printf(" Start Balance   : ₹%.2f%n", finalBalance);
                System.out.printf(" Date Range      : %s to %s%n", startDate, endDate);
                System.out.printf(" Statement File  : %s%n", targetFile.getName());
                if (targetUserFile != null && targetUserFile.exists()) {
                    System.out.printf(" User Details    : %s%n", targetUserFile.getName());
                }
                System.out.println("-------------------------------------------------");
                System.out.println(" Status          : SUCCESS");
                System.out.printf(" Ending Balance  : ₹%.2f%n", endBalance);
                System.out.printf(" Statement Path  : %s%n", targetFile.getAbsolutePath());
                if (targetUserFile != null && targetUserFile.exists()) {
                    System.out.printf(" User Details Path: %s%n", targetUserUserPathOrAbs(targetUserFile));
                }
                System.out.println("=================================================");
            }

            return 0;
        } catch (Exception e) {
            System.err.println("Error generating transactions: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    private String targetUserUserPathOrAbs(File f) {
        return f != null ? f.getAbsolutePath() : "N/A";
    }

    private File resolveOutputFile(String pathInput, String autoFileName, boolean isMultiSource) {
        if (pathInput == null || pathInput.isBlank()) {
            return new File(autoFileName);
        }

        File inputPath = new File(pathInput);

        boolean isDirectory = isMultiSource
                || inputPath.isDirectory()
                || pathInput.endsWith("/")
                || pathInput.endsWith("\\")
                || (!inputPath.getName().contains(".") && !inputPath.exists());

        if (isDirectory) {
            if (!inputPath.exists()) {
                inputPath.mkdirs();
            }
            return new File(inputPath, autoFileName);
        } else {
            return inputPath;
        }
    }

    private File resolveUserOutputFile(String pathInput, File targetStatementFile, String autoUserFileName, boolean isMultiSource) {
        if (pathInput == null || pathInput.isBlank()) {
            return new File(autoUserFileName);
        }

        File inputPath = new File(pathInput);

        boolean isDirectory = isMultiSource
                || inputPath.isDirectory()
                || pathInput.endsWith("/")
                || pathInput.endsWith("\\")
                || (!inputPath.getName().contains(".") && !inputPath.exists());

        if (isDirectory) {
            if (!inputPath.exists()) {
                inputPath.mkdirs();
            }
            return new File(inputPath, autoUserFileName);
        } else {
            String parentDir = targetStatementFile.getParent();
            String stmtName = targetStatementFile.getName();
            String userFileName = stmtName.toLowerCase().endsWith(".csv")
                    ? stmtName.substring(0, stmtName.length() - 4) + "_user_details.csv"
                    : stmtName + "_user_details.csv";
            return parentDir != null ? new File(parentDir, userFileName) : new File(userFileName);
        }
    }
}

