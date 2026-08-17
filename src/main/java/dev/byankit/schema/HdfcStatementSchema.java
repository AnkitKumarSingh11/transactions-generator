package dev.byankit.schema;

import dev.byankit.enums.TransactionSource;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;

import java.util.List;

public class HdfcStatementSchema extends AbstractStatementSchema {

    @Override
    public TransactionSource getSource() {
        return TransactionSource.HDFC;
    }

    @Override
    public List<String> getHeaders() {
        return List.of(
                "Date", "Narration Value Dat", "Debit Amount", "Credit Amount", "Chq/Ref Number", "Closing Balance"
        );
    }

    @Override
    public List<String> formatRecord(Transaction tx, StatementConfig config) {
        String formattedDate = formatDate(tx.getDate(), config);
        String narration = tx.getNarration() != null ? tx.getNarration() : "";
        String debit = formatAmount(tx.getDebitAmount());
        String credit = formatAmount(tx.getCreditAmount());
        String refNum = tx.getRefNumber() != null ? tx.getRefNumber() : "";
        String closingBal = formatBalance(tx.getClosingBalance());

        return List.of(formattedDate, narration, debit, credit, refNum, closingBal);
    }

    @Override
    public String getDefaultDateFormat() {
        return "dd/MM/yy";
    }
}
