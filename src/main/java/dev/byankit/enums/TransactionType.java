package dev.byankit.enums;

public enum TransactionType {
    BANKSTATEMENT("File containing bank statement transactions"),
    UPISERVICE("For files containing UPI transactions");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static TransactionType fromString(String typeStr) {
        if (typeStr == null || typeStr.isBlank()) {
            return BANKSTATEMENT;
        }
        for (TransactionType type : TransactionType.values()) {
            if (type.name().equalsIgnoreCase(typeStr.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + typeStr + ". Supported types: BANKSTATEMENT, UPISERVICE");
    }
}
