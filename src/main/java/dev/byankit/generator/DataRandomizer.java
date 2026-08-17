package dev.byankit.generator;

import dev.byankit.enums.TransactionCategory;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DataRandomizer {
    private final Faker faker;
    private final Random random;

    private static final List<String> MERCHANTS = List.of(
            "Zomato India", "Swiggy", "Amazon Retail", "Flipkart India", "Uber India",
            "Ola Cabs", "Blinkit", "Zepto", "D-Mart Supermarket", "BookMyShow",
            "Starbucks Coffee", "Reliance Fresh", "Apollo Pharmacy", "MakeMyTrip"
    );

    private static final List<String> BANKS = List.of(
            "HDFC", "ICICI", "SBI", "AXIS", "KOTAK", "PAYTM", "PNB"
    );

    private static final List<String> UPI_HANDLES = List.of(
            "okicici", "okhdfcbank", "oksbi", "ybl", "paytm", "apl", "upi"
    );

    private static final List<String> CITIES = List.of(
            "MUMBAI", "DELHI", "BANGALORE", "HYDERABAD", "CHENNAI", "KOLKATA", "PUNE", "AHMEDABAD"
    );

    public DataRandomizer() {
        this.faker = new Faker(new Locale("en", "IN"));
        this.random = new Random();
    }

    public DataRandomizer(long seed) {
        this.faker = new Faker(new Locale("en", "IN"), new Random(seed));
        this.random = new Random(seed);
    }

    public String generatePersonName() {
        return faker.name().fullName().toUpperCase();
    }

    public String generateMerchantName() {
        return MERCHANTS.get(random.nextInt(MERCHANTS.size())).toUpperCase();
    }

    public String generateUtrNumber() {
        // 12-digit UTR/RRN number
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9) + 1);
        for (int i = 0; i < 11; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public String generateNeftRefNumber() {
        // NEFT reference number format like N12345678901
        return "N" + generateUtrNumber().substring(0, 11);
    }

    public String generateChequeRefNumber() {
        // 6-digit Cheque number or 12-digit Ref number
        if (random.nextBoolean()) {
            return String.format("%06d", random.nextInt(1000000));
        } else {
            return generateUtrNumber();
        }
    }

    public TransactionCategory getRandomCategory() {
        // Weighted distribution: UPI is most frequent in modern statements
        int roll = random.nextInt(100);
        if (roll < 45) {
            return TransactionCategory.UPI;
        } else if (roll < 60) {
            return TransactionCategory.CARD_PAYMENT;
        } else if (roll < 75) {
            return TransactionCategory.NEFT;
        } else if (roll < 85) {
            return TransactionCategory.IMPS;
        } else if (roll < 92) {
            return TransactionCategory.ATM_WITHDRAWAL;
        } else if (roll < 97) {
            return TransactionCategory.BILL_PAYMENT;
        } else {
            return TransactionCategory.SALARY;
        }
    }

    public String generateNarration(TransactionCategory category, boolean isDebit, String refNum) {
        String bank = BANKS.get(random.nextInt(BANKS.size()));
        String city = CITIES.get(random.nextInt(CITIES.size()));

        switch (category) {
            case UPI: {
                String party = isDebit ? (random.nextBoolean() ? generateMerchantName() : generatePersonName()) : generatePersonName();
                String handle = party.toLowerCase().replaceAll("[^a-z]", "") + "@" + UPI_HANDLES.get(random.nextInt(UPI_HANDLES.size()));
                return String.format("UPI-%s-%s-%s-%s", refNum, party, bank, handle);
            }
            case NEFT: {
                String party = isDebit ? generateMerchantName() : generatePersonName();
                String type = isDebit ? "DR" : "CR";
                return String.format("NEFT %s-%s-%s", type, refNum, party);
            }
            case IMPS: {
                String party = isDebit ? generatePersonName() : generatePersonName();
                return String.format("IMPS-%s-%s-%s", refNum, party, bank);
            }
            case ATM_WITHDRAWAL: {
                return String.format("ATM WDL-%s BANK-%s", bank, city);
            }
            case CARD_PAYMENT: {
                String merchant = generateMerchantName();
                String maskedCard = "451234******" + String.format("%04d", random.nextInt(10000));
                return String.format("POS %s %s %s", maskedCard, merchant, city);
            }
            case SALARY: {
                String company = faker.company().name().toUpperCase();
                return String.format("ACH C- %s SALARY %s", company, refNum.substring(0, 6));
            }
            case BILL_PAYMENT: {
                return String.format("ACH D- UTILITY BILL %s REF-%s", city, refNum.substring(0, 6));
            }
            case INTEREST_CREDIT: {
                return "CREDIT INTEREST CALCULATED UPTO " + LocalDate.now().toString();
            }
            default:
                return "TRF TO " + generatePersonName();
        }
    }

    public double generateAmount(TransactionCategory category, boolean isDebit) {
        switch (category) {
            case UPI:
                // Small to medium amounts: 20 to 5000
                return Math.round((20.0 + random.nextDouble() * 4980.0) * 100.0) / 100.0;
            case CARD_PAYMENT:
                // 150 to 12000
                return Math.round((150.0 + random.nextDouble() * 11850.0) * 100.0) / 100.0;
            case ATM_WITHDRAWAL:
                // Multiples of 500/1000: 500 to 10000
                return (random.nextInt(20) + 1) * 500.0;
            case NEFT:
            case IMPS:
                // 1000 to 50000
                return Math.round((1000.0 + random.nextDouble() * 49000.0) * 100.0) / 100.0;
            case SALARY:
                // 35000 to 250000
                return Math.round((35000.0 + random.nextDouble() * 215000.0) * 100.0) / 100.0;
            case BILL_PAYMENT:
                // 300 to 4500
                return Math.round((300.0 + random.nextDouble() * 4200.0) * 100.0) / 100.0;
            case INTEREST_CREDIT:
                // 50 to 1200
                return Math.round((50.0 + random.nextDouble() * 1150.0) * 100.0) / 100.0;
            default:
                return Math.round((100.0 + random.nextDouble() * 9900.0) * 100.0) / 100.0;
        }
    }

    public LocalDate generateRandomDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return startDate;
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) {
            return startDate;
        }
        long randomDays = (long) (random.nextDouble() * (days + 1));
        return startDate.plusDays(randomDays);
    }

    public Random getRandom() {
        return random;
    }
}
