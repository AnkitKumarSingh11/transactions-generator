package dev.byankit.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenerateCommandTest {

    @Test
    void testExecuteCliHdfcGenerateCommand() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "finstream_test_dir");
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        String[] args = {
                "--source", "HDFC",
                "--count", "10",
                "--initial-balance", "25000.00",
                "--output", tempDir.getAbsolutePath(),
                "--start-date", "2023-01-01",
                "--end-date", "2023-01-31"
        };

        int exitCode = new CommandLine(new GenerateCommand()).execute(args);

        assertEquals(0, exitCode);
        File[] stmtFiles = tempDir.listFiles((dir, name) -> name.endsWith("_HDFC_statement.csv"));
        assertNotNull(stmtFiles);
        assertTrue(stmtFiles.length > 0, "Should auto-generate statement filename inside specified directory");
        assertTrue(stmtFiles[0].length() > 0);

        File[] userFiles = tempDir.listFiles((dir, name) -> name.endsWith("_user_details.csv"));
        assertNotNull(userFiles);
        assertTrue(userFiles.length > 0, "Should auto-generate user details filename inside specified directory");
        assertTrue(userFiles[0].length() > 0);

        List<String> lines = Files.readAllLines(userFiles[0].toPath());
        assertFalse(lines.isEmpty());
        assertTrue(lines.get(0).contains("Name"));
        assertTrue(lines.get(0).contains("Email"));
        assertTrue(lines.get(0).contains("Account Number"));
    }

    @Test
    void testExecuteCliDefaultSourceAndUserDetailsOutput() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "default_test_dir");
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        String customName = "Amina Khan";
        String customAccNo = "50100123456789";

        String[] args = {
                "--count", "15",
                "--name", customName,
                "--account-number", customAccNo,
                "--output", tempDir.getAbsolutePath()
        };

        int exitCode = new CommandLine(new GenerateCommand()).execute(args);
        assertEquals(0, exitCode);

        File expectedStmtFile = new File(tempDir, "AMINA_KHAN_50100123456789_HDFC_statement.csv");
        assertTrue(expectedStmtFile.exists(), "Default source should create HDFC statement file in target directory");

        File expectedUserFile = new File(tempDir, "AMINA_KHAN_50100123456789_user_details.csv");
        assertTrue(expectedUserFile.exists(), "User details file should be created in target directory");
        assertTrue(expectedUserFile.length() > 0);
    }
}
