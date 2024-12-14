import demo.DemoProject;
import framework.context.QuickLinkContext;
import framework.setup.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class PerformanceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTest.class);

    private void setup(){
        QuickLinkContext context = new QuickLinkContext(DemoProject.class);
        ComponentScanner.scanComponentsAndInterceptables(context);
        GraphChecker.checkCycles(context);
        InjectableFactory.instantiateSingletons(context);
        ControllerMapper.mapControllersToUrls(context);
        ControllerMethodMapper.mapHandlersForRequests(context);
        CallResolver.setup(context);
    }

    @Test
    public void testSetup(){
        Instant start = Instant.now();
        setup();
        Instant end = Instant.now();
        LOGGER.info("Setup took {} ms", end.toEpochMilli() - start.toEpochMilli());
    }

    @Test
    public void testRegularMethod() {
        setup();
        int callCount = 1000;

        Instant start = Instant.now();
        for(int i = 0; i < callCount; i++){
            CallResolver.handleCall("/resource/1");
        }
        Instant end = Instant.now();
        LOGGER.info("Regular method took {} ms for {} calls", end.toEpochMilli() - start.toEpochMilli(), callCount);
    }

    @Test
    public void testTimedMethod() {
        setup();
        int callCount = 1000;

        Instant start = Instant.now();
        for(int i = 0; i < callCount; i++){
            CallResolver.handleCall("/warehouse/find/Mark/iron_ore");
        }
        Instant end = Instant.now();
        LOGGER.info("Timed method took {} ms for {} calls", end.toEpochMilli() - start.toEpochMilli(), callCount);
    }
}
