package demotests;

import demo.DemoProject;
import demo.config.OutputConverterConfig;
import framework.context.QuickLinkContext;
import framework.context.RunMode;
import framework.request.response.ContentType;
import framework.request.response.HttpResponse;
import framework.setup.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WarehouseTests {

    private void setup(RunMode runMode) {
        if(runMode == RunMode.CONSOLE){
            // Simulate demo setup
            OutputConverterConfig.setIsConsole(true);
        }

        QuickLinkContext context = new QuickLinkContext(DemoProject.class);
        ComponentScanner.scanComponentsAndInterceptables(context);
        GraphChecker.checkCycles(context);
        InjectableFactory.instantiateSingletons(context);
        ControllerMapper.mapControllersToUrls(context);
        ControllerMethodMapper.mapHandlersForRequests(context);
        CallResolver.setup(context);
    }

    @Test
    public void coloredText(){
        // Given i run in console
        setup(RunMode.CONSOLE);

        // When i execute a call
        HttpResponse response = CallResolver.handleCall("/warehouses/Mark/iron_ore");

        // Then the response is yellow
        Assertions.assertTrue(response.getBody().startsWith("\u001B[33m"));
    }

    @Test
    public void httpNotColored(){
        // Given i run in http
        setup(RunMode.HTTP);

        // When i execute a call
        HttpResponse response = CallResolver.handleCall("/warehouses/Mark/iron_ore");

        // Then the response is not yellow
        Assertions.assertFalse(response.getBody().startsWith("\u001B[33m"));
    }

    @Test
    public void jsonConverter(){
        // Given the context is set up
        setup(RunMode.HTTP);

        // When i execute a call that returns JSON
        HttpResponse response = CallResolver.handleCall("/warehouses/as-json/Mark/iron_ore");

        // Then i get a json response
        Assertions.assertTrue(response.getBody().startsWith("{"));
        Assertions.assertSame(response.getContentType(), ContentType.JSON);
    }

    @Test
    public void htmlConverter(){
        // Given the context is set up
        setup(RunMode.HTTP);

        // When i execute a call that returns HTML
        HttpResponse response = CallResolver.handleCall("/warehouses/as-html/Mark/iron_ore");

        // Then i get a html response
        Assertions.assertTrue(response.getBody().startsWith("<html>"));
        Assertions.assertSame(response.getContentType(), ContentType.HTML);
    }


}
