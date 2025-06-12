package framework.context.config;

import framework.setup.model.reflection.annotated_entities.InjectableClassWithInterceptedMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.Collectors;

public class LogFormatter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFormatter.class);

    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String HIGHLIGHT_PREFIX = String.format("%n%n%s", ANSI_CYAN);
    private static final String HIGHLIGHT_SUFFIX = String.format("%n%s", ANSI_RESET);

    public String highlight(String message) {
        return String.format("%s%s%s", HIGHLIGHT_PREFIX, message, HIGHLIGHT_SUFFIX);
    }

    public void logTimedMethodScanComplete(Collection<InjectableClassWithInterceptedMethods<?>> timedMethods) {
        if (!LOGGER.isDebugEnabled()) return;

        String timedMethodScanCompleteMessage = highlight("Timed method scanning complete. Entries are: \n{}");

        LOGGER.debug(timedMethodScanCompleteMessage, timedMethods.stream()
                .map(injectable -> String.format("| -> %-100s |%n%s", injectable.classType().toString(),
                        injectable.annotatedMethods().stream()
                                .map(method -> String.format("|        %-96s |", method.toString()))
                                .collect(Collectors.joining("\n"))))
                .collect(Collectors.joining("\n")));
    }

    public void logComponentScanComplete(Collection<?> components) {
        if (!LOGGER.isDebugEnabled()) return;

        String componentScanCompleteMessage = highlight("Component scanning complete. Entries are: \n{}");

        LOGGER.debug(componentScanCompleteMessage, components.stream()
                .map(component -> String.format("| - %-100s |", component.toString()))
                .collect(Collectors.joining("\n")));
    }
}
