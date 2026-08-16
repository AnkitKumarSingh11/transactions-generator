package dev.byankit.enums;

public enum TransactionSource {
    paytm("Paytm App"),
    payzap("HDFC PayZapp App"),
    hdfc("HDFC Bank"),
    sbi("SBI Bank");

    private final String name;

    TransactionSource(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
