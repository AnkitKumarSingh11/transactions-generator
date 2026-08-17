package dev.byankit.model;

import dev.byankit.enums.TransactionSource;
import dev.byankit.enums.TransactionType;
import java.time.LocalDate;

public class StatementConfig {
    private final TransactionSource source;
    private final TransactionType type;
    private final int count;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double initialBalance;
    private final String outputPath;
    private final String dateFormat;
    private final String accountHolderName;
    private final String accountNumber;
    private final String paytmId;

    public StatementConfig(TransactionSource source, TransactionType type, int count,
                           LocalDate startDate, LocalDate endDate, double initialBalance,
                           String outputPath, String dateFormat,
                           String accountHolderName, String accountNumber, String paytmId) {
        this.source = source != null ? source : TransactionSource.HDFC;
        this.type = type != null ? type : TransactionType.BANKSTATEMENT;
        this.count = count > 0 ? count : 50;
        this.startDate = startDate != null ? startDate : LocalDate.now().minusDays(30);
        this.endDate = endDate != null ? endDate : LocalDate.now();
        this.initialBalance = initialBalance >= 0 ? initialBalance : 50000.00;
        this.outputPath = outputPath;
        this.dateFormat = (dateFormat != null && !dateFormat.isBlank()) ? dateFormat : "dd/MM/yyyy";
        this.accountHolderName = accountHolderName;
        this.accountNumber = accountNumber;
        this.paytmId = paytmId;
    }

    public TransactionSource getSource() {
        return source;
    }

    public TransactionType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPaytmId() {
        return paytmId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TransactionSource source = TransactionSource.HDFC;
        private TransactionType type = TransactionType.BANKSTATEMENT;
        private int count = 50;
        private LocalDate startDate = LocalDate.now().minusDays(30);
        private LocalDate endDate = LocalDate.now();
        private double initialBalance = 50000.00;
        private String outputPath = null;
        private String dateFormat = "dd/MM/yyyy";
        private String accountHolderName = null;
        private String accountNumber = null;
        private String paytmId = null;

        public Builder source(TransactionSource source) {
            this.source = source;
            return this;
        }

        public Builder type(TransactionType type) {
            this.type = type;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder initialBalance(double initialBalance) {
            this.initialBalance = initialBalance;
            return this;
        }

        public Builder outputPath(String outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder dateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public Builder accountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder paytmId(String paytmId) {
            this.paytmId = paytmId;
            return this;
        }

        public StatementConfig build() {
            return new StatementConfig(source, type, count, startDate, endDate, initialBalance, outputPath, dateFormat, accountHolderName, accountNumber, paytmId);
        }
    }
}
