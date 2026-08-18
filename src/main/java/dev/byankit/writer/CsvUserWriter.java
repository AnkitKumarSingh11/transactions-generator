package dev.byankit.writer;

import dev.byankit.model.PersonProfile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CsvUserWriter {

    public static final List<String> HEADERS = List.of(
            "Name",
            "Email",
            "Mobile Number",
            "Account Number",
            "IFSC Code",
            "UPI ID",
            "Customer ID",
            "Company Name",
            "Monthly Salary"
    );

    public void writeToFile(PersonProfile profile, File targetFile) throws IOException {
        if (profile == null || targetFile == null) {
            return;
        }

        if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        try (Writer writer = new BufferedWriter(new FileWriter(targetFile));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader(HEADERS.toArray(new String[0]))
                     .build())) {

            List<String> record = List.of(
                    profile.getFullName() != null ? profile.getFullName() : "",
                    profile.getEmail() != null ? profile.getEmail() : "",
                    profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "",
                    profile.getAccountNumber() != null ? profile.getAccountNumber() : "",
                    profile.getIfscCode() != null ? profile.getIfscCode() : "",
                    profile.getUpiHandle() != null ? profile.getUpiHandle() : "",
                    profile.getCustomerId() != null ? profile.getCustomerId() : "",
                    profile.getCompanyName() != null ? profile.getCompanyName() : "",
                    String.format("%.2f", profile.getMonthlySalary())
            );

            csvPrinter.printRecord(record);
            csvPrinter.flush();
        }
    }
}
