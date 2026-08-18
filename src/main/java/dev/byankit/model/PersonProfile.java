package dev.byankit.model;

import dev.byankit.generator.DataRandomizer;

public class PersonProfile {
    private final String fullName;
    private final String email;
    private final String phoneNumber;
    private final String accountNumber;
    private final String ifscCode;
    private final String upiHandle;
    private final String customerId;
    private final String companyName;
    private final double monthlySalary;
    private final double monthlyRent;
    private final int salaryCreditDay;
    private final int rentPaymentDay;
    private final int utilityPaymentDay;

    public PersonProfile(DataRandomizer randomizer, double minSalary, double maxSalary) {
        this(randomizer, minSalary, maxSalary, null, null);
    }

    public PersonProfile(DataRandomizer randomizer, double minSalary, double maxSalary,
                         String customName, String customAccountNumber) {
        if (customName != null && !customName.isBlank()) {
            this.fullName = customName.trim().toUpperCase();
        } else {
            this.fullName = randomizer.generatePersonName();
        }

        if (customAccountNumber != null && !customAccountNumber.isBlank()) {
            this.accountNumber = customAccountNumber.trim();
        } else {
            this.accountNumber = "50100" + String.format("%09d", randomizer.getRandom().nextInt(1000000000));
        }

        String sanitizedName = fullName.toLowerCase().replaceAll("[^a-z]", "");
        int randomSuffix = 100 + randomizer.getRandom().nextInt(900);
        this.email = (sanitizedName.isEmpty() ? "user" : sanitizedName) + randomSuffix + "@example.com";
        this.phoneNumber = "+91" + (7 + randomizer.getRandom().nextInt(3)) + String.format("%09d", randomizer.getRandom().nextInt(1000000000));

        this.ifscCode = "HDFC000" + String.format("%04d", randomizer.getRandom().nextInt(10000));
        this.upiHandle = sanitizedName + "@okhdfcbank";
        this.customerId = "CUST" + String.format("%07d", randomizer.getRandom().nextInt(10000000));

        String[] companies = {"TCS", "INFOSYS", "WIPRO", "ACCENTURE", "GOOGLE INDIA", "FLIPKART", "AMAZON INDIA", "HDFC BANK"};
        this.companyName = companies[randomizer.getRandom().nextInt(companies.length)];

        double salaryRange = maxSalary - minSalary;
        this.monthlySalary = Math.round((minSalary + randomizer.getRandom().nextDouble() * salaryRange) * 100.0) / 100.0;
        this.monthlyRent = Math.round((monthlySalary * 0.25) * 100.0) / 100.0;

        this.salaryCreditDay = 1 + randomizer.getRandom().nextInt(4); // 1st to 4th of month
        this.rentPaymentDay = 5 + randomizer.getRandom().nextInt(3);  // 5th to 7th of month
        this.utilityPaymentDay = 10 + randomizer.getRandom().nextInt(5); // 10th to 14th of month
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public String getUpiHandle() {
        return upiHandle;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getMonthlySalary() {
        return monthlySalary;
    }

    public double getMonthlyRent() {
        return monthlyRent;
    }

    public int getSalaryCreditDay() {
        return salaryCreditDay;
    }

    public int getRentPaymentDay() {
        return rentPaymentDay;
    }

    public int getUtilityPaymentDay() {
        return utilityPaymentDay;
    }
}

