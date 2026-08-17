package dev.byankit.enums;

public enum TransactionSource {
    HDFC("HDFC Bank"),
    SBI("SBI Bank"),
    PAYTM("Paytm App"),
    PAYZAPP("HDFC PayZapp App");

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
        throw new IllegalArgumentException("Unknown transaction source: " + sourceStr + ". Supported sources: HDFC, SBI, PAYTM, PAYZAPP");
    }
}
