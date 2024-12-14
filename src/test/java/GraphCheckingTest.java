import testprojects.cyclicalproject.CyclicalProjectMain;
import testprojects.cyclicalproject.service.CyclicalService1;
import testprojects.cyclicalproject.service.CyclicalService2;
import testprojects.cyclicalproject.service.CyclicalService3;
import framework.context.QuickLinkContext;
import framework.exceptions.cycles.CyclicalDependencyException;
import framework.setup.ComponentScanner;
import framework.setup.GraphChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testprojects.testproject.DummyProjectMain;

import java.util.Arrays;
import java.util.HashSet;

public class GraphCheckingTest {
    @Test
    public void testCyclical(){
        // Given a context with a cyclical dependency, with scanned components
        QuickLinkContext context = new QuickLinkContext(CyclicalProjectMain.class);
        ComponentScanner.scanComponentsAndInterceptables(context);

        // When i test for cyclical dependencies
        // Then it finds the cyclical dependency
        CyclicalDependencyException exception = Assertions.assertThrows(CyclicalDependencyException.class, ()-> GraphChecker.checkCycles(context));

        // And it identifies the classes in the cycle.
        Assertions.assertEquals(exception.getClasses(), new HashSet<>(Arrays.asList(CyclicalService1.class, CyclicalService2.class, CyclicalService3.class)));
    }

    @Test
    public void testNotCyclical(){
        // Given a context without cyclical dependencies, with components
        QuickLinkContext context = new QuickLinkContext(DummyProjectMain.class);
        ComponentScanner.scanComponentsAndInterceptables(context);

        // When i test for cyclical dependencies
        // Then it doesn't find a cyclical dependency
        Assertions.assertDoesNotThrow(()-> GraphChecker.checkCycles(context));
    }
}
