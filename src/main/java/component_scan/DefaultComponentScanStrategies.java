package component_scan;

import component_scan.annotations.Injectable;
import component_scan.annotations.clarification.PrimaryConstructor;
import component_scan.annotations.injection.config.Bean;
import component_scan.annotations.injection.config.Config;
import component_scan.annotations.injection.semantic.Controller;
import component_scan.annotations.injection.semantic.Repository;
import component_scan.annotations.injection.semantic.Service;
import helper.ConstructorFinder;
import component_scan.strategies.contracts.AnnotationReflectionStrategy;
import component_scan.strategies.contracts.ComponentSupplier;
import component_scan.strategies.contracts.InjectableScanStrategy;
import component_scan.strategies.implementations.AnnotationBasedInjectableScanStrategy;
import component_scan.strategies.implementations.component.AnnotatedClassComponentSupplier;
import component_scan.strategies.implementations.component.AnnotatedMethodComponentSupplier;
import component_scan.strategies.implementations.component.ClassMethodComponentSupplier;
import component_scan.strategies.implementations.component.CombinedAnnotationsComponentSupplier;
import framework.setup.model.reflection.annotation.AnnotationSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public class DefaultComponentScanStrategies {
    private DefaultComponentScanStrategies() {}

    public static ComponentSupplier beanSupplier(
            AnnotationReflectionStrategy annotationReflectionStrategy,
            ConstructorFinder constructorFinder
    ) {
        return new AnnotatedMethodComponentSupplier(
                AnnotationSet.of(Config.class),
                AnnotationSet.of(Bean.class),
                annotationReflectionStrategy,
                constructorFinder
        );
    }

    public static ComponentSupplier injectableSupplier(
            InjectableScanStrategy injectableScanStrategy,
            ConstructorFinder constructorFinder
    ) {
        return new AnnotatedClassComponentSupplier(
                injectableScanStrategy,
                constructorFinder
        );
    }

    public static ComponentSupplier defaultComponentSupplier(
            Class<?> methodComponentClass,
            ConstructorFinder constructorFinder
    ) {
        return new ClassMethodComponentSupplier<>(
                methodComponentClass,
                constructorFinder
        );
    }

    public static InjectableScanStrategy injectableScanStrategy(AnnotationReflectionStrategy annotationReflectionStrategy) {
        Collection<Class<? extends Annotation>> injectableAnnotations = Set.of(
                Injectable.class,
                Repository.class,
                Service.class,
                Controller.class
        );

        return new AnnotationBasedInjectableScanStrategy(
                annotationReflectionStrategy,
                AnnotationSet.of(injectableAnnotations)
        );
    }

    public static ConstructorFinder constructorFinder() {
        Predicate<Constructor<?>> isPrimary = c -> c.getAnnotation(PrimaryConstructor.class) != null;
        return new ConstructorFinder(isPrimary);
    }

    public static ComponentSupplier componentSupplier(
            Class<?> methodComponentClass,
            ConstructorFinder constructorFinder,
            AnnotationReflectionStrategy annotationReflectionStrategy,
            InjectableScanStrategy injectableScanStrategy
    ) {
        return new CombinedAnnotationsComponentSupplier(
                Set.of(
                        beanSupplier(annotationReflectionStrategy, constructorFinder),
                        injectableSupplier(injectableScanStrategy, constructorFinder)
                ),
                defaultComponentSupplier(methodComponentClass, constructorFinder)
        );
    }
}
