package framework.setup.strategies;

import component_scan.DefaultComponentScanStrategies;
import component_scan.annotations.interception.Timed;
import component_scan.strategies.contracts.ComponentSupplier;
import framework.configurables.conversions.impl.DefaultConfigurationMappings;
import framework.context.config.ComponentScanScope;
import framework.context.config.QuickLinkStrategies;
import framework.setup.model.reflection.annotation.AnnotationSet;
import component_scan.strategies.contracts.AnnotationReflectionStrategy;
import component_scan.strategies.contracts.InjectableScanStrategy;
import framework.setup.strategies.contracts.InterceptMethodScanStrategy;
import framework.setup.strategies.implementations.InterceptMethodScanStrategyImpl;
import reflection.DefaultReflectionFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

public class DefaultStrategies {
    private DefaultStrategies() {}

    public static InterceptMethodScanStrategy interceptMethodScanStrategy(
            AnnotationReflectionStrategy annotationReflectionStrategy,
            InjectableScanStrategy injectableScanStrategy
    ) {
        Collection<Class<? extends Annotation>> interceptMethodAnnotations = Set.of(
                Timed.class
        );

        AnnotationSet annotationSet = AnnotationSet.of(interceptMethodAnnotations);
        return new InterceptMethodScanStrategyImpl(
                annotationReflectionStrategy,
                injectableScanStrategy,
                annotationSet
        );
    }

    public static QuickLinkStrategies strategies(ComponentScanScope scope) {
        AnnotationReflectionStrategy annotationReflectionStrategy = DefaultReflectionFactory.componentScanStrategy(scope);
        InjectableScanStrategy injectableScanStrategy = DefaultComponentScanStrategies.injectableScanStrategy(annotationReflectionStrategy);

        InterceptMethodScanStrategy interceptMethodScanStrategy = interceptMethodScanStrategy(
                annotationReflectionStrategy,
                injectableScanStrategy
        );

        ComponentSupplier componentSupplier = DefaultComponentScanStrategies.componentSupplier(
                DefaultConfigurationMappings.class,
                DefaultComponentScanStrategies.constructorFinder(),
                annotationReflectionStrategy,
                DefaultComponentScanStrategies.injectableScanStrategy(annotationReflectionStrategy)
        );

        return new QuickLinkStrategies(
                interceptMethodScanStrategy,
                componentSupplier
        );

    }
}
