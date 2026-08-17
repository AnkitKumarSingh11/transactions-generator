package dev.byankit.schema;

import dev.byankit.enums.TransactionSource;
import dev.byankit.model.StatementConfig;
import dev.byankit.model.Transaction;

import java.util.List;
import java.util.Random;

public class PaytmStatementSchema extends AbstractStatementSchema {
    private final Random random = new Random(42);

    private static final List<String> TAGS = List.of(
            "#🛒 Groceries", "#🍔 Food & Dining", "#Bills & Utilities", "#Shopping", "#Travel", "#P2P Transfer"
    );

    private static final List<String> YOUR_ACCOUNTS = List.of(
            "State Bank Of India - 04", "HDFC Bank - 12", "ICICI Bank - 88", "Axis Bank - 45", "Paytm Payments Bank - 99"
    );

    @Override
    public TransactionSource getSource() {
        return TransactionSource.PAYTM;
    }

    @Override
    public List<String> getHeaders() {
        return List.of(
                "Date",
                "Time",
                "Transaction Details",
                "Other Transaction Details (UPI ID or A/c No)",
                "Your Account",
                "Amount",
                "UPI Ref No.",
                "Order ID",
                "Remarks",
                "Tags",
                "Comment"
        );
    }

    @Override
    public List<String> formatRecord(Transaction tx, StatementConfig config) {
        String dateStr = formatDate(tx.getDate(), config);

        // Realistic random time HH:mm:ss
        int hour = 8 + random.nextInt(14); // 08:00 to 22:00
        int min = random.nextInt(60);
        int sec = random.nextInt(60);
        String timeStr = String.format("%02d:%02d:%02d", hour, min, sec);

        String narration = tx.getNarration() != null ? tx.getNarration() : "";
        boolean isDebit = tx.isDebit();

        String details;
        if (narration.startsWith("UPI-") || narration.startsWith("POS ") || narration.startsWith("NEFT ") || narration.startsWith("IMPS-")) {
            String[] parts = narration.split("-");
            String party = parts.length > 2 ? parts[2] : narration;
            details = isDebit ? "Paid to " + party : "Received from " + party;
        } else if (narration.contains("SALARY")) {
            details = "Salary Credit from " + narration;
        } else if (narration.contains("INTEREST")) {
            details = "Cashback from Paytm";
        } else {
            details = isDebit ? "Paid to " + narration : "Received from " + narration;
        }

        String paytmId = (config != null && config.getPaytmId() != null && !config.getPaytmId().isBlank())
                ? config.getPaytmId()
                : "paytm.user@pty";
        String otherDetails = paytmId + " on Paytm";

        String yourAccount = YOUR_ACCOUNTS.get(random.nextInt(YOUR_ACCOUNTS.size()));

        Double rawAmt = isDebit ? tx.getDebitAmount() : tx.getCreditAmount();
        double amtVal = rawAmt != null ? rawAmt : 0.0;
        String amtStr = isDebit ? String.format("-%.2f", amtVal) : String.format("%.2f", amtVal);

        String refNo = tx.getRefNumber() != null ? tx.getRefNumber() : "";
        String orderId = (refNo.length() >= 6) ? "202608" + refNo : "";
        String remarks = "";
        String tag = TAGS.get(random.nextInt(TAGS.size()));
        String comment = "";

        return List.of(dateStr, timeStr, details, otherDetails, yourAccount, amtStr, refNo, orderId, remarks, tag, comment);
    }

    @Override
    public String getDefaultDateFormat() {
        return "dd/MM/yyyy";
    }
}
