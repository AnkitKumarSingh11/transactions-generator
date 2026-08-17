package dev.byankit.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class GenerateCommandTest {

    @Test
    void testExecuteCliGenerateCommand() throws Exception {
        File tempFile = File.createTempFile("cli_test_statement", ".csv");
        tempFile.deleteOnExit();

        String[] args = {
                "--source", "HDFC",
                "--count", "10",
                "--initial-balance", "25000.00",
                "--output", tempFile.getAbsolutePath(),
                "--start-date", "2023-01-01",
                "--end-date", "2023-01-31"
        };

        int exitCode = new CommandLine(new GenerateCommand()).execute(args);

        assertEquals(0, exitCode);
        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
    }

    @Test
    void testExecuteCliCustomNameAndAccountNumber() throws Exception {
        String customName = "Ankit Kumar Singh";
        String customAccNo = "50100987654321";

        String[] args = {
                "--source", "HDFC",
                "--count", "15",
                "--name", customName,
                "--account-number", customAccNo
        };

        int exitCode = new CommandLine(new GenerateCommand()).execute(args);
        assertEquals(0, exitCode);

        String expectedFileName = "ANKIT_KUMAR_SINGH_50100987654321_HDFC_statement.csv";
        File generatedFile = new File(expectedFileName);
        assertTrue(generatedFile.exists());
        assertTrue(generatedFile.length() > 0);
        generatedFile.deleteOnExit();
    }
}
