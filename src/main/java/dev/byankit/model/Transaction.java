package dev.byankit.model;

import dev.byankit.enums.TransactionCategory;
import java.time.LocalDate;
import java.util.Objects;

public class Transaction {
    private final LocalDate date;
    private final LocalDate valueDate;
    private final String narration;
    private final Double debitAmount;
    private final Double creditAmount;
    private final String refNumber;
    private final Double closingBalance;
    private final TransactionCategory category;

    public Transaction(LocalDate date, LocalDate valueDate, String narration,
                       Double debitAmount, Double creditAmount, String refNumber,
                       Double closingBalance, TransactionCategory category) {
        this.date = date;
        this.valueDate = valueDate;
        this.narration = narration;
        this.debitAmount = debitAmount;
        this.creditAmount = creditAmount;
        this.refNumber = refNumber;
        this.closingBalance = closingBalance;
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public String getNarration() {
        return narration;
    }

    public Double getDebitAmount() {
        return debitAmount;
    }

    public Double getCreditAmount() {
        return creditAmount;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public Double getClosingBalance() {
        return closingBalance;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public boolean isDebit() {
        return debitAmount != null && debitAmount > 0;
    }

    public boolean isCredit() {
        return creditAmount != null && creditAmount > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(valueDate, that.valueDate) &&
                Objects.equals(narration, that.narration) &&
                Objects.equals(debitAmount, that.debitAmount) &&
                Objects.equals(creditAmount, that.creditAmount) &&
                Objects.equals(refNumber, that.refNumber) &&
                Objects.equals(closingBalance, that.closingBalance) &&
                category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, valueDate, narration, debitAmount, creditAmount, refNumber, closingBalance, category);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", valueDate=" + valueDate +
                ", narration='" + narration + '\'' +
                ", debitAmount=" + debitAmount +
                ", creditAmount=" + creditAmount +
                ", refNumber='" + refNumber + '\'' +
                ", closingBalance=" + closingBalance +
                ", category=" + category +
                '}';
    }
}
