package framework.setup;

import framework.context.QuickLinkContext;
import framework.context.config.LogFormatter;
import framework.context.config.QuickLinkStrategies;
import framework.setup.model.Component;
import framework.setup.model.reflection.annotated_entities.InjectableClassWithInterceptedMethods;

import java.util.*;

public class ComponentScanner {

    private ComponentScanner() {}

    public static void scanComponentsAndInterceptables(QuickLinkContext context) {
        LogFormatter logFormatter = context.getLogFormatter();
        QuickLinkStrategies strategies = context.getStrategies();

        Collection<InjectableClassWithInterceptedMethods<?>> timedMethods = strategies
                .interceptMethodScanStrategy()
                .getInterceptedMethods();

        context.getCache().setTimedMethods(timedMethods);
        logFormatter.logTimedMethodScanComplete(timedMethods);

        Collection<Component> components = strategies
                .componentSupplier()
                .getComponents();

        context.getCache().setComponents(components);
        logFormatter.logComponentScanComplete(components);
    }

}
