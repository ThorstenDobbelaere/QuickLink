package tests;


import framework.context.QuickLinkContext;
import framework.request.response.HttpResponse;
import framework.request.response.HttpStatus;
import framework.setup.CallResolver;
import framework.setup.ComponentScanner;
import framework.setup.ControllerMapper;
import framework.setup.ControllerMethodMapper;
import framework.setup.GraphChecker;
import framework.setup.InjectableFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testprojects.testproject.DummyProjectMain;

class CallResolverTest {

    private void setup() {
        QuickLinkContext context = new QuickLinkContext(DummyProjectMain.class);
        ComponentScanner.scanComponentsAndInterceptables(context);
        GraphChecker.checkCycles(context);
        InjectableFactory.instantiateSingletons(context);
        ControllerMapper.mapControllersToUrls(context);
        ControllerMethodMapper.mapHandlersForRequests(context);
        CallResolver.setup(context);
    }

    @Test
    void nonExistentCall() {
        // Given the setup for dummy project is complete
        setup();

        // When i look for a non-existent call
        HttpResponse response = CallResolver.handleCall("nonexistent");

        // Then it returns bad request
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    void workingCall() {
        // Given the setup for dummy project is complete
        setup();

        // When i look for a method in the controller
        HttpResponse response = CallResolver.handleCall("/dummy/pet");

        // Then it returns the expected response
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Assertions.assertEquals("Pet[owner=Person[name=Bartje, age=22], name=Max]", response.getBody());
    }
}
