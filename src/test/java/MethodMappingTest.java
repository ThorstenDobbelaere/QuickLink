import ambiguityproject.AmbiguityProjectMain;
import emptymappingproject.EmptyMappingProjectMain;
import framework.context.QuickLinkContext;
import framework.exceptions.componentscan.DuplicateException;
import framework.exceptions.componentscan.MappingException;
import framework.setup.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testproject.DummyProjectMain;

public class MethodMappingTest {
    private void setup(Class<?> baseClass){
        QuickLinkContext context = new QuickLinkContext(baseClass);
        ComponentScanner.scanComponentsAndInterceptables(context);
        GraphChecker.checkCycles(context);
        InjectableFactory.instantiateSingletons(context);
        ControllerMapper.mapControllersToUrls(context);
        ControllerMethodMapper.mapHandlersForRequests(context);
    }

    @Test
    public void ambiguousMappings(){
        // Given a project with ambiguous mappings
        Class<?> baseClass = AmbiguityProjectMain.class;

        // When i try to run the setup
        // Then i get a DuplicateException
        Assertions.assertThrows(DuplicateException.class, () -> setup(baseClass));
    }

    @Test
    public void goodMappings(){
        // Given a project with well-defined mappings
        Class<?> baseClass = DummyProjectMain.class;

        // When i try to run the setup
        // Then i don't get any exception
        setup(baseClass);
    }

    @Test
    public void emptyMapping(){
        // Given a project with an empty mapping
        Class<?> baseClass = EmptyMappingProjectMain.class;

        // When i try to run the setup
        // Then i get a MappingException
        Assertions.assertThrows(MappingException.class, () -> setup(baseClass));
    }
}
