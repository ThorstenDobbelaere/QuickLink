package tests;

import component_scan.annotations.Injectable;
import component_scan.annotations.injection.semantic.Controller;
import component_scan.annotations.injection.semantic.Repository;
import component_scan.annotations.injection.semantic.Service;
import component_scan.annotations.mapping.IOMapping;
import component_scan.annotations.mapping.InputMapping;
import component_scan.annotations.mapping.OutputMapping;
import framework.setup.model.reflection.annotated_entities.AnnotatedMethod;
import framework.setup.model.reflection.annotated_entities.InjectableClass;
import framework.setup.model.reflection.annotation.AnnotationSet;
import framework.setup.strategies.contracts.ComponentScanStrategy;
import framework.setup.strategies.implementations.ReflectionsComponentScanStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import testprojects.testproject.DummyProjectMain;
import testprojects.testproject.controller.DummyController;
import testprojects.testproject.repo.AgeRepository;
import testprojects.testproject.repo.PetNameRepository;
import testprojects.testproject.service.PetNameService;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

class ReflectionsComponentScanStrategyTest {

    @Test
    void scanClassesWithAnnotations_shouldReturnCorrectResult() {
        // Given a reflections-based component scanning strategy for the dummy project
        Collection<InjectableClass<?>> dummyFolderInjectables = scanClassesWithAnnotationsUsingReflections(Set.of(
                Injectable.class,
                Repository.class,
                Service.class,
                Controller.class
        ));

        // When i look for components
        // Optional<InjectableClass<?>> personInjectable = getOptionalInjectable(Person.class, dummyFolderInjectables);
        Optional<InjectableClass<?>> componentScanTestComponent = getOptionalInjectable(ReflectionsComponentScanStrategyTest.class, dummyFolderInjectables);

        // Then I find the controllers with the right mapping
        assertAnnotationEquals(dummyFolderInjectables, DummyController.class, Controller.class);
        assertAnnotationEquals(dummyFolderInjectables, PetNameService.class, Service.class);
        assertAnnotationEquals(dummyFolderInjectables, AgeRepository.class, Repository.class);
        assertAnnotationEquals(dummyFolderInjectables, PetNameRepository.class, Repository.class);

        // And the result has the correct length
        Assertions.assertEquals(4, dummyFolderInjectables.size());

        // And i don't find other classes
        Assertions.assertTrue(componentScanTestComponent.isEmpty());
    }

    @Test
    void scanMethodsWithAnnotations_givenClass_shouldReturnCorrectResult() {
        // Given a reflections-based component scanning strategy for the dummy project
        Collection<AnnotatedMethod> dummyFolderMethods = scanMethodsWithAnnotationsUsingReflections(
                DummyController.class,
                Set.of(OutputMapping.class, InputMapping.class, IOMapping.class)
        );

        // When i look for methods
        Optional<AnnotatedMethod> methodOptional = dummyFolderMethods.stream()
                .filter(method -> method.method().getName().equals("getPet"))
                .findFirst();

        // Then I find the method with the right annotation
        Assertions.assertTrue(methodOptional.isPresent());
        AnnotatedMethod annotatedMethod = methodOptional.get();
        Assertions.assertSame(OutputMapping.class, annotatedMethod.annotationType().annotation());

        // And the result has the correct length
        Assertions.assertEquals(1, dummyFolderMethods.size());
    }

    // === Helpers ===

    private static Optional<InjectableClass<?>> getOptionalInjectable(Class<?> type, Collection<InjectableClass<?>> injectables) {
        return injectables.stream()
                .filter(injectableClass -> injectableClass.classType() == type)
                .findFirst();
    }

    private static void assertAnnotationEquals(
            Collection<InjectableClass<?>> injectables,
            Class<?> type,
            Class<? extends Annotation> annotation
    ) {
        Optional<InjectableClass<?>> injectableOptional = getOptionalInjectable(type, injectables);
        InjectableClass<?> injectable = injectableOptional.orElseThrow();
        Assertions.assertSame(annotation, injectable.annotationType().annotation());
    }

    // ==== Scanning methods ===

    private static Collection<InjectableClass<?>> scanClassesWithAnnotationsUsingReflections(
            Collection<Class<? extends Annotation>> annotations
    ) {
        Reflections reflections = new Reflections(DummyProjectMain.class.getPackage().getName());
        ComponentScanStrategy strategy = new ReflectionsComponentScanStrategy(reflections);
        AnnotationSet annotationSet = new AnnotationSet(annotations);
        return strategy.getClassesAnnotatedWith(annotationSet);
    }

    private static Collection<AnnotatedMethod> scanMethodsWithAnnotationsUsingReflections(
            Class<?> type,
            Collection<Class<? extends Annotation>> annotations
    ) {
        Reflections reflections = new Reflections(DummyProjectMain.class.getPackage().getName());
        ComponentScanStrategy strategy = new ReflectionsComponentScanStrategy(reflections);
        AnnotationSet annotationSet = new AnnotationSet(annotations);
        return strategy.getMethodsAnnotatedWith(type, annotationSet);
    }
}
