package dev.byankit.enums;

public enum TransactionCategory {
    UPI("UPI Payment"),
    NEFT("NEFT Transfer"),
    IMPS("IMPS Transfer"),
    ATM_WITHDRAWAL("ATM Cash Withdrawal"),
    CARD_PAYMENT("Debit/Credit Card POS Payment"),
    SALARY("Salary Credit"),
    BILL_PAYMENT("Utility Bill Payment"),
    INTEREST_CREDIT("Bank Interest Credit");

    private final String description;

    TransactionCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
