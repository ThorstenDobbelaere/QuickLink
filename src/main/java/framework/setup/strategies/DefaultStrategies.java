package framework.setup.strategies;

import framework.annotations.Injectable;
import framework.annotations.injection.semantic.Controller;
import framework.annotations.injection.semantic.Repository;
import framework.annotations.injection.semantic.Service;
import framework.context.QuickLinkContext;
import framework.setup.model.reflection.annotation.AnnotationSet;
import framework.setup.strategies.contracts.ComponentScanStrategy;
import framework.setup.strategies.contracts.InjectableScanStrategy;
import framework.setup.strategies.implementations.AnnotationBasedInjectableScanStrategy;
import framework.setup.strategies.implementations.ReflectionsComponentScanStrategy;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

public class DefaultStrategies {
    private DefaultStrategies() {}

    public static InjectableScanStrategy injectableScanStrategy(ComponentScanStrategy strategy) {
        Collection<Class<? extends Annotation>> injectableAnnotations = Set.of(
                Injectable.class,
                Repository.class,
                Service.class,
                Controller.class
        );
        return new AnnotationBasedInjectableScanStrategy(strategy, new AnnotationSet(injectableAnnotations));
    }

    public static ComponentScanStrategy componentScanStrategy(Reflections reflections) {
        return new ReflectionsComponentScanStrategy(reflections);
    }
}
