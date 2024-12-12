import cyclicalproject.CyclicalProjectMain;
import cyclicalproject.service.CyclicalService1;
import cyclicalproject.service.CyclicalService2;
import cyclicalproject.service.CyclicalService3;
import framework.context.QuickLinkContext;
import framework.exceptions.scanning.CyclicalDependencyException;
import framework.setup.ComponentScanner;
import framework.setup.GraphChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
}
