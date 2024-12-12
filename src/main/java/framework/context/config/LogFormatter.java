package framework.context.config;

public class LogFormatter {
    public LogFormatter() {}

    private final String ansiCyan = "\u001B[36m";
    private final String ansiReset = "\u001B[0m";
    private final String highlightPrefix = String.format("\n\n%s", ansiCyan);
    private final String highlightSuffix = String.format("\n%s", ansiReset);

    public String highlight(String message) {
        return String.format("%s%s%s", highlightPrefix, message, highlightSuffix);
    }
}
