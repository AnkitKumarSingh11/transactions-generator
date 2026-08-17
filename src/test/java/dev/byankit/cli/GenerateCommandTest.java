package dev.byankit.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class GenerateCommandTest {

    @Test
    void testExecuteCliHdfcGenerateCommand() throws Exception {
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
    void testExecuteCliPaytmGenerateCommand() throws Exception {
        String customName = "Amina Khan";
        String customPaytmId = "paytm.s1mdx1j@pty";

        String[] args = {
                "--source", "PAYTM",
                "--count", "15",
                "--name", customName,
                "--paytm-id", customPaytmId
        };

        int exitCode = new CommandLine(new GenerateCommand()).execute(args);
        assertEquals(0, exitCode);

        String expectedFileName = "AMINA_KHAN_paytm.s1mdx1j_pty_PAYTM_statement.csv";
        File generatedFile = new File(expectedFileName);
        assertTrue(generatedFile.exists());
        assertTrue(generatedFile.length() > 0);
        generatedFile.deleteOnExit();
    }
}
