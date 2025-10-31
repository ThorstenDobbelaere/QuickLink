package tests;

import framework.QuickLink;
import framework.context.QuickLinkContext;
import framework.request.response.HttpResponse;
import framework.request.response.HttpStatus;
import framework.setup.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testprojects.testproject.DummyProjectMain;

public class CallResolverTest {

    private void setup(){
        QuickLinkContext context = new QuickLinkContext(DummyProjectMain.class);
        new QuickLink(context)
            .scanComponents()
            .checkCycles()
            .instantiateSingletons()
            .mapControllers()
            .mapControllerMethods()
            .setupCallResolver();
    }

    @Test
    public void nonExistentCall() {
        // Given the setup for dummy project is complete
        setup();

        // When i look for a non-existent call
        HttpResponse response = CallResolver.handleCall("nonexistent");

        // Then it returns bad request
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    public void workingCall() {
        // Given the setup for dummy project is complete
        setup();

        // When i look for a method in the controller
        HttpResponse response = CallResolver.handleCall("/dummy/pet");

        // Then it returns the expected response
        Assertions.assertEquals(HttpStatus.OK, response.getStatus());
        Assertions.assertEquals(response.getBody(), "Pet[owner=Person[name=Bartje, age=22], name=Max]");
    }
}
