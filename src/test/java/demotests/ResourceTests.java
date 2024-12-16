package demotests;

import demo.DemoProject;
import framework.context.QuickLinkContext;
import framework.request.response.ContentType;
import framework.request.response.HttpResponse;
import framework.request.response.HttpStatus;
import framework.setup.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ResourceTests {
    @BeforeEach
    public void setup(){
        // Given the demo project is set up
        QuickLinkContext context = new QuickLinkContext(DemoProject.class);
        ComponentScanner.scanComponentsAndInterceptables(context);
        GraphChecker.checkCycles(context);
        InjectableFactory.instantiateSingletons(context);
        ControllerMapper.mapControllersToUrls(context);
        ControllerMethodMapper.mapHandlersForRequests(context);
        CallResolver.setup(context);
    }

    @Test
    public void getResource(){
        // When i get a resource
        HttpResponse response = CallResolver.handleCall("/resources/1");

        // Then i get status OK
        Assertions.assertSame(HttpStatus.OK, response.getStatus());

        // And i get content type plain
        Assertions.assertSame(ContentType.PLAIN, response.getContentType());

        // And i get the expected response body
        Assertions.assertTrue(response.getBody().contains("Resource[name=Iron Ore, referenceName=iron_ore, price=10.0]"));
    }

    @Test
    public void createIOResource(){
        // When i create a resource with IO mapping
        HttpResponse response = CallResolver.handleCall("/resources/create-io/Sugar/sugar/10.0");

        // Then i get the correct return
        Assertions.assertSame(HttpStatus.OK, response.getStatus());
        Assertions.assertSame(ContentType.PLAIN, response.getContentType());
        Assertions.assertEquals("Resource[name=Sugar, referenceName=sugar, price=10.0]", response.getBody());

        // And the resource exists in the database
        assertResourceExists("sugar");
    }

    @Test
    public void createInputResource(){
        // When i create a resource with input mapping
        HttpResponse response = CallResolver.handleCall("/resources/create-input/Coal/coal/5.5");

        // Then i get the correct return
        Assertions.assertSame(HttpStatus.NO_CONTENT, response.getStatus());
        Assertions.assertSame(ContentType.PLAIN, response.getContentType());
        Assertions.assertTrue(response.getBody().isEmpty());

        // And the resource exists in the database
        assertResourceExists("coal");
    }

    @Test
    public void getResourceNotExists(){
        // When i look for a resource that doesn't exist
        HttpResponse response = CallResolver.handleCall("/resources/-1");

        // Then I get no content
        Assertions.assertSame(HttpStatus.NO_CONTENT, response.getStatus());
        Assertions.assertSame(ContentType.PLAIN, response.getContentType());
        Assertions.assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void getResources(){
        // When i search for all resources
        HttpResponse response = CallResolver.handleCall("/resources/all");

        // Then i get ok
        Assertions.assertSame(HttpStatus.OK, response.getStatus());
        Assertions.assertSame(ContentType.PLAIN, response.getContentType());

        // And i get 3 resources
        int resourceCount = response.getBody().split("Resource\\[").length - 1;
        Assertions.assertEquals(3, resourceCount);
    }

    @Test
    public void getExistingResourceByName(){
        // When i look for an existing resource
        // Then it returns the resource
        assertResourceExists("iron_ore");
    }

    @Test
    public void getNonExistingResourceByName(){
        // When i look for a non-existing resource
        // Then i get no content
        assertResourceNotExists("gold_ore");
    }

    @Test
    public void deleteResourceByName(){
        // Given i deleted iron ore from the resource list
        CallResolver.handleCall("/resources/delete/iron_ore");

        // When i look for iron ore
        // Then i don't find it
        assertResourceNotExists("iron_ore");
    }

    private void assertResourceExists(String referenceName){
        HttpResponse response = CallResolver.handleCall("/resources/name/" + referenceName);
        Assertions.assertSame(HttpStatus.OK, response.getStatus());
        Assertions.assertSame(ContentType.PLAIN, response.getContentType());
        Assertions.assertTrue(response.getBody().contains(referenceName));
    }

    private void assertResourceNotExists(String referenceName){
        HttpResponse response = CallResolver.handleCall("/resources/name/" + referenceName);
        Assertions.assertSame(HttpStatus.NO_CONTENT, response.getStatus());
        Assertions.assertSame(ContentType.PLAIN, response.getContentType());
        Assertions.assertFalse(response.getBody().contains(referenceName));
    }

}
