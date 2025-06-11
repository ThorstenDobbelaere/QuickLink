package tests;

import component_scan.exceptions.DuplicateException;
import component_scan.exceptions.MappingException;
import framework.context.QuickLinkContext;
import framework.setup.ComponentScanner;
import framework.setup.ControllerMapper;
import framework.setup.ControllerMethodMapper;
import framework.setup.GraphChecker;
import framework.setup.InjectableFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testprojects.ambiguityproject.AmbiguityProjectMain;
import testprojects.emptymappingproject.EmptyMappingProjectMain;
import testprojects.testproject.DummyProjectMain;

class MethodMappingTest {
    private void setup(Class<?> baseClass){
        QuickLinkContext context = new QuickLinkContext(baseClass);
        ComponentScanner.scanComponentsAndInterceptables(context);
        GraphChecker.checkCycles(context);
        InjectableFactory.instantiateSingletons(context);
        ControllerMapper.mapControllersToUrls(context);
        ControllerMethodMapper.mapHandlersForRequests(context);
    }

    @Test
    void ambiguousMappings(){
        // Given a project with ambiguous mappings
        Class<?> baseClass = AmbiguityProjectMain.class;

        // When i try to run the setup
        // Then i get a DuplicateException
        Assertions.assertThrows(DuplicateException.class, () -> setup(baseClass));
    }

    @Test
    void goodMappings(){
        // Given a project with well-defined mappings
        Class<?> baseClass = DummyProjectMain.class;

        // When i try to run the setup
        // Then i don't get any exception
        setup(baseClass);
    }

    @Test
    void emptyMapping(){
        // Given a project with an empty mapping
        Class<?> baseClass = EmptyMappingProjectMain.class;

        // When i try to run the setup
        // Then i get a MappingException
        Assertions.assertThrows(MappingException.class, () -> setup(baseClass));
    }
}
