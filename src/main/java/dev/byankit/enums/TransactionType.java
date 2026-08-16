package dev.byankit.enums;

public enum TransactionType {
    upiservice("For files containing UPI transactions"),
    wallet("File containing wallet payments transactions"),
    bankstatement("File containing bank transactions");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
