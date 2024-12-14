import framework.context.QuickLinkContext;
import framework.setup.ComponentScanner;
import framework.setup.model.Component;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testprojects.testproject.DummyProjectMain;
import testprojects.testproject.controller.DummyController;
import testprojects.testproject.model.Person;
import testprojects.testproject.repo.AgeRepository;
import testprojects.testproject.service.PetNameService;

import java.util.Optional;

public class ComponentScanningTest {

    private static Optional<Component> getOptionalComponent(Class<?> type, QuickLinkContext context) {
        return context.getCache()
                .getComponents()
                .stream()
                .filter(component -> component.getType().equals(type))
                .findFirst();
    }

    @Test
    public void testControllerScan() {
        // Given the component scanning of a project is finished
        QuickLinkContext context = new QuickLinkContext(DummyProjectMain.class);
        ComponentScanner.scanComponentsAndInterceptables(context);

        // When i look for components
        Optional<Component> dummyControllerComponentOptional = getOptionalComponent(DummyController.class, context);
        Optional<Component> personComponent = getOptionalComponent(Person.class, context);
        Optional<Component> petNameServiceComponent = getOptionalComponent(PetNameService.class, context);
        Optional<Component> ageRepositoryComponent = getOptionalComponent(AgeRepository.class, context);
        Optional<Component> componentScanTestComponent = getOptionalComponent(ComponentScanningTest.class, context);

        // Then i find the controllers with the right mapping
        var dummyControllerComponent = dummyControllerComponentOptional.orElseThrow();
        Assertions.assertTrue(dummyControllerComponent.isController());
        Assertions.assertEquals(dummyControllerComponent.getControllerPath(), "/dummy");

        // And i find the other components
        Assertions.assertTrue(personComponent.isPresent());
        Assertions.assertTrue(petNameServiceComponent.isPresent());
        Assertions.assertTrue(ageRepositoryComponent.isPresent());

        // And i don't find other classes
        Assertions.assertTrue(componentScanTestComponent.isEmpty());

        // And i find the correct number of timed methods.
        Assertions.assertEquals(2, context.getCache().getTimedMethods().entrySet().size());
    }


}
