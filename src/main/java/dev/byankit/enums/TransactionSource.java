package dev.byankit.enums;

public enum TransactionSource {
    HDFC("HDFC Bank"),
    SBI("SBI Bank"),
    PAYTM("Paytm Wallet"),
    PAYZAPP("HDFC PayZapp Wallet");

    private final String name;

    TransactionSource(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static TransactionSource fromString(String sourceStr) {
        if (sourceStr == null || sourceStr.isBlank()) {
            return HDFC;
        }
        for (TransactionSource src : TransactionSource.values()) {
            if (src.name().equalsIgnoreCase(sourceStr.trim()) || src.getName().equalsIgnoreCase(sourceStr.trim())) {
                return src;
            }
        }
        return HDFC;
    }
}
