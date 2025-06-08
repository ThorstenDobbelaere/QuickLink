package tests;

import framework.context.QuickLinkContext;
import framework.setup.ControllerMapper;
import framework.setup.InjectableFactory;
import framework.setup.model.Component;
import framework.setup.model.MappedController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testprojects.testproject.DummyProjectMain;
import testprojects.testproject.config.PersonConfig;
import testprojects.testproject.config.PrimitiveConfig;
import testprojects.testproject.controller.DummyController;
import testprojects.testproject.model.Person;
import testprojects.testproject.model.Pet;
import testprojects.testproject.repo.AgeRepository;
import testprojects.testproject.repo.PetNameRepository;
import testprojects.testproject.service.PetNameService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class InjectionAndUrlMappingTest {

    private QuickLinkContext setupContext() throws NoSuchMethodException {
        QuickLinkContext context = new QuickLinkContext(DummyProjectMain.class);

        Set<Component> components = new HashSet<>();
        components.add(Component.forConstructor(AgeRepository.class.getConstructor()));
        components.add(Component.forConstructor(PetNameRepository.class.getConstructor()));
        components.add(Component.forConstructor(PetNameService.class.getConstructor(PetNameRepository.class)));
        components.add(Component.forConstructor(DummyController.class.getConstructor(PetNameService.class, Person.class)));

        PersonConfig personConfig = new PersonConfig();
        components.add(new Component(PersonConfig.class.getMethod("createPerson", String.class, Integer.class), personConfig));

        PrimitiveConfig primitiveConfig = new PrimitiveConfig();
        components.add(new Component(PrimitiveConfig.class.getMethod("ownerName"), primitiveConfig));
        components.add(new Component(PrimitiveConfig.class.getMethod("age", AgeRepository.class), primitiveConfig));

        context.getCache().setComponents(components);
        return context;
    }

    @Test
    void testBeanAndComponentMapping() throws NoSuchMethodException {
        // Given a context for the test project
        var context = setupContext();

        // When i instantiate the classes and map the controllers
        InjectableFactory.instantiateSingletons(context);
        ControllerMapper.mapControllersToUrls(context);
        Collection<Component> generatedComponents = context.getCache().getComponents();
        Set<MappedController> mappedControllers = context.getCache().getMappedControllers();

        // Then there's a DummyController component
        Optional<Component> component = generatedComponents.stream().filter(c->c.getType().equals(DummyController.class)).findFirst();
        Assertions.assertTrue(component.isPresent());
        DummyController dummyController = (DummyController) component.get().getInstance();

        // And it returns the expected value
        Pet pet = dummyController.getPet();
        Assertions.assertEquals(new Pet(new Person("Bartje", 22), "Max"), pet);

        // And there's a controller mapped to /dummy
        Optional<MappedController> mappedController = mappedControllers.stream().filter(mc->mc.mapping().equals("/dummy")).findFirst();
        Assertions.assertTrue(mappedController.isPresent());

        // And it maps to a DummyController object
        Object controller = mappedController.get().controller();
        Assertions.assertInstanceOf(DummyController.class, controller);
    }

}
