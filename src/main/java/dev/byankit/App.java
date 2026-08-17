package dev.byankit;

import dev.byankit.cli.GenerateCommand;
import picocli.CommandLine;

public class App {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GenerateCommand()).execute(args);
        System.exit(exitCode);
    }
}
