package framework.setup;

import component_scan.annotations.injection.config.Bean;
import component_scan.annotations.injection.config.Config;
import component_scan.helper.ConstructorFinder;
import framework.configurables.conversions.impl.DefaultConfigurationMappings;
import framework.context.QuickLinkContext;
import framework.context.config.LogFormatter;
import framework.context.config.QuickLinkStrategies;
import framework.setup.model.Component;
import framework.setup.model.reflection.annotated_entities.InjectableClassWithInterceptedMethods;
import framework.setup.model.reflection.annotation.AnnotationSet;
import component_scan.strategies.contracts.AnnotationReflectionStrategy;
import component_scan.strategies.contracts.ComponentSupplier;
import framework.setup.strategies.contracts.InterceptMethodScanStrategy;
import component_scan.strategies.implementations.component.AnnotatedClassComponentSupplier;
import component_scan.strategies.implementations.component.AnnotatedMethodComponentSupplier;
import component_scan.strategies.implementations.component.ClassMethodComponentSupplier;
import component_scan.strategies.implementations.component.CombinedAnnotationsComponentSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ComponentScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentScanner.class);

    private ComponentScanner() {}

    public static void scanComponentsAndInterceptables(QuickLinkContext context) {
        LogFormatter logFormatter = context.getLogFormatter();
        QuickLinkStrategies strategies = context.getStrategies();
        AnnotationReflectionStrategy annotationReflectionStrategy = strategies.annotationReflectionStrategy();
        InterceptMethodScanStrategy interceptMethodScanStrategy = strategies.interceptMethodScanStrategy();

        var injectableScanStrategy = strategies.injectableScanStrategy();

        var timedMethods = interceptMethodScanStrategy.getInterceptedMethods();
        context.getCache().setTimedMethods(timedMethods);
        logTimedMethodScanCompleteMessage(logFormatter, timedMethods);
        ConstructorFinder constructorFinder = strategies.constructorFinder();

        ComponentSupplier beans = new AnnotatedMethodComponentSupplier(
                AnnotationSet.of(Config.class),
                AnnotationSet.of(Bean.class),
                annotationReflectionStrategy,
                constructorFinder
        );

        ComponentSupplier injectables = new AnnotatedClassComponentSupplier(
                injectableScanStrategy,
                constructorFinder
        );

        ComponentSupplier defaultComponents = new ClassMethodComponentSupplier<>(
                DefaultConfigurationMappings.class,
                constructorFinder
        );

        ComponentSupplier combinedComponentSupplier = new CombinedAnnotationsComponentSupplier(
                Arrays.asList(beans, injectables),
                defaultComponents
        );

        Collection<Component> components = combinedComponentSupplier.getComponents();
        context.getCache().setComponents(components);
        logComponentScanCompleteMessage(logFormatter, components);
    }

    private static void logTimedMethodScanCompleteMessage(
            LogFormatter logFormatter,
            Collection<InjectableClassWithInterceptedMethods<?>> timedMethods
    ) {
        if (!LOGGER.isDebugEnabled()) return;

        String timedMethodScanCompleteMessage = logFormatter.highlight("Timed method scanning complete. Entries are: \n{}");

        LOGGER.debug(timedMethodScanCompleteMessage, timedMethods.stream()
                .map(injectable -> String.format("| -> %-100s |%n%s", injectable.classType().toString(),
                        injectable.annotatedMethods().stream()
                                .map(method -> String.format("|        %-96s |", method.toString()))
                                .collect(Collectors.joining("\n"))))
                .collect(Collectors.joining("\n")));
    }

    private static void logComponentScanCompleteMessage(LogFormatter logFormatter, Collection<Component> components) {
        if (!LOGGER.isDebugEnabled()) return;

        String componentScanCompleteMessage = logFormatter.highlight("Component scanning complete. Entries are: \n{}");

        LOGGER.debug(componentScanCompleteMessage, components.stream()
                .map(component -> String.format("| - %-100s |", component.getType()))
                .collect(Collectors.joining("\n")));

    }


}
