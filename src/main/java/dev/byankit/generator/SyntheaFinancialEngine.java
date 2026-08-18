package dev.byankit.generator;

import dev.byankit.config.AppConfig;
import dev.byankit.enums.TransactionCategory;
import dev.byankit.model.PersonProfile;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SyntheaFinancialEngine {
    private final DataRandomizer randomizer;
    private final AppConfig appConfig;
    private PersonProfile lastGeneratedProfile;

    public SyntheaFinancialEngine(DataRandomizer randomizer, AppConfig appConfig) {
        this.randomizer = randomizer != null ? randomizer : new DataRandomizer();
        this.appConfig = appConfig != null ? appConfig : new AppConfig();
    }

    public List<Transaction> simulateFinancialLifecycle(StatementConfig config) {
        double minSalary = appConfig.getDouble("profile.default.min_salary", 45000.00);
        double maxSalary = appConfig.getDouble("profile.default.max_salary", 180000.00);

        PersonProfile profile = new PersonProfile(
                randomizer,
                minSalary,
                maxSalary,
                config.getAccountHolderName(),
                config.getAccountNumber()
        );
        this.lastGeneratedProfile = profile;

        LocalDate startDate = config.getStartDate();
        LocalDate endDate = config.getEndDate();
        int targetCount = config.getCount();
        double currentBalance = config.getInitialBalance();

        List<Transaction> candidateTransactions = new ArrayList<>();

        // Iterate month by month from startDate to endDate
        LocalDate currentMonth = startDate.withDayOfMonth(1);
        LocalDate lastMonth = endDate.withDayOfMonth(1);

        while (!currentMonth.isAfter(lastMonth)) {
            // 1. Monthly Salary Credit Event
            int salaryDay = Math.min(profile.getSalaryCreditDay(), currentMonth.lengthOfMonth());
            LocalDate salaryDate = currentMonth.withDayOfMonth(salaryDay);
            if (!salaryDate.isBefore(startDate) && !salaryDate.isAfter(endDate)) {
                String salRef = randomizer.generateChequeRefNumber();
                String narration = String.format("ACH C- %s SALARY %s", profile.getCompanyName(), salRef.substring(0, 6));
                candidateTransactions.add(new Transaction(
                        salaryDate, salaryDate, narration, null, profile.getMonthlySalary(), salRef, 0.0, TransactionCategory.SALARY
                ));
            }

            // 2. Monthly Rent Payment Event
            int rentDay = Math.min(profile.getRentPaymentDay(), currentMonth.lengthOfMonth());
            LocalDate rentDate = currentMonth.withDayOfMonth(rentDay);
            if (!rentDate.isBefore(startDate) && !rentDate.isAfter(endDate)) {
                String rentRef = randomizer.generateNeftRefNumber();
                String landlord = randomizer.generatePersonName();
                String narration = String.format("NEFT DR-%s-HOUSE RENT TO %s", rentRef, landlord);
                candidateTransactions.add(new Transaction(
                        rentDate, rentDate, narration, profile.getMonthlyRent(), null, rentRef, 0.0, TransactionCategory.NEFT
                ));
            }

            // 3. Monthly Utility Bill Payment Event
            int utilDay = Math.min(profile.getUtilityPaymentDay(), currentMonth.lengthOfMonth());
            LocalDate utilDate = currentMonth.withDayOfMonth(utilDay);
            if (!utilDate.isBefore(startDate) && !utilDate.isAfter(endDate)) {
                String utilRef = randomizer.generateUtrNumber();
                double utilAmt = Math.round((1200.0 + randomizer.getRandom().nextDouble() * 3800.0) * 100.0) / 100.0;
                String narration = String.format("ACH D- ELECTRICITY & BROADBAND BILL REF-%s", utilRef.substring(0, 6));
                candidateTransactions.add(new Transaction(
                        utilDate, utilDate, narration, utilAmt, null, utilRef, 0.0, TransactionCategory.BILL_PAYMENT
                ));
            }

            // 4. Quarterly Savings Interest Credit (March, June, Sept, Dec)
            if (currentMonth.getMonthValue() % 3 == 0) {
                LocalDate interestDate = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth());
                if (!interestDate.isBefore(startDate) && !interestDate.isAfter(endDate)) {
                    double interestAmt = Math.round((250.0 + randomizer.getRandom().nextDouble() * 1250.0) * 100.0) / 100.0;
                    String ref = randomizer.generateChequeRefNumber();
                    String narration = "CREDIT INTEREST CALCULATED UPTO " + interestDate.toString();
                    candidateTransactions.add(new Transaction(
                            interestDate, interestDate, narration, null, interestAmt, ref, 0.0, TransactionCategory.INTEREST_CREDIT
                    ));
                }
            }

            currentMonth = currentMonth.plusMonths(1);
        }

        // 5. Fill remaining quota with daily micro transactions (UPI, POS Cards, ATM)
        int missingCount = targetCount - candidateTransactions.size();
        if (missingCount < 0) {
            Collections.sort(candidateTransactions, Comparator.comparing(Transaction::getDate));
            candidateTransactions = new ArrayList<>(candidateTransactions.subList(0, targetCount));
        } else {
            for (int i = 0; i < missingCount; i++) {
                LocalDate date = randomizer.generateRandomDateBetween(startDate, endDate);
                TransactionCategory cat = randomizer.getRandomCategory();
                if (cat == TransactionCategory.SALARY) {
                    cat = TransactionCategory.UPI;
                }

                boolean isDebit = (cat != TransactionCategory.INTEREST_CREDIT) && (randomizer.getRandom().nextInt(100) < 75);
                double amount = randomizer.generateAmount(cat, isDebit);
                String refNum = randomizer.generateChequeRefNumber();
                String narration = randomizer.generateNarration(cat, isDebit, refNum);

                Double debit = isDebit ? amount : null;
                Double credit = !isDebit ? amount : null;

                candidateTransactions.add(new Transaction(
                        date, date, narration, debit, credit, refNum, 0.0, cat
                ));
            }
        }

        // Sort all transactions chronologically
        Collections.sort(candidateTransactions, Comparator.comparing(Transaction::getDate));

        // Recompute running balance accurately
        List<Transaction> finalTransactions = new ArrayList<>();
        double runningBal = currentBalance;

        for (Transaction rawTx : candidateTransactions) {
            boolean isDebit = rawTx.isDebit();
            double amt = isDebit ? rawTx.getDebitAmount() : (rawTx.getCreditAmount() != null ? rawTx.getCreditAmount() : 0.0);

            if (isDebit && amt > runningBal) {
                if (runningBal > 100) {
                    amt = Math.round((randomizer.getRandom().nextDouble() * (runningBal - 50.0)) * 100.0) / 100.0;
                } else {
                    isDebit = false;
                    amt = Math.round((500.0 + randomizer.getRandom().nextDouble() * 2500.0) * 100.0) / 100.0;
                }
            }

            Double finalDebit = isDebit ? amt : null;
            Double finalCredit = !isDebit ? amt : null;

            if (isDebit) {
                runningBal -= amt;
            } else {
                runningBal += amt;
            }

            runningBal = Math.round(runningBal * 100.0) / 100.0;

            finalTransactions.add(new Transaction(
                    rawTx.getDate(),
                    rawTx.getValueDate(),
                    rawTx.getNarration(),
                    finalDebit,
                    finalCredit,
                    rawTx.getRefNumber(),
                    runningBal,
                    rawTx.getCategory()
            ));
        }

        return finalTransactions;
    }

    public PersonProfile getLastGeneratedProfile() {
        return lastGeneratedProfile;
    }
}
