import framework.context.QuickLinkContext;
import framework.resolver.ObjectMapper;
import framework.resolver.model.Component;
import framework.resolver.model.MappedController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testproject.DummyProjectMain;
import testproject.config.PersonConfig;
import testproject.config.PrimitiveConfig;
import testproject.controller.DummyController;
import testproject.model.Person;
import testproject.model.Pet;
import testproject.repo.AgeRepository;
import testproject.repo.PetNameRepository;
import testproject.service.PetNameService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ObjectsAndControllersMappingTest {

    private QuickLinkContext setupContext() throws NoSuchMethodException {
        QuickLinkContext context = new QuickLinkContext(DummyProjectMain.class);

        Set<Component> components = new HashSet<>();
        components.add(new Component(AgeRepository.class.getConstructor()));
        components.add(new Component(PetNameRepository.class.getConstructor()));
        components.add(new Component(PetNameService.class.getConstructor(PetNameRepository.class)));
        components.add(new Component(DummyController.class.getConstructor(PetNameService.class, Person.class)));

        PersonConfig personConfig = new PersonConfig();
        components.add(new Component(PersonConfig.class.getMethod("createPerson", String.class, Integer.class), personConfig));

        PrimitiveConfig primitiveConfig = new PrimitiveConfig();
        components.add(new Component(PrimitiveConfig.class.getMethod("ownerName"), primitiveConfig));
        components.add(new Component(PrimitiveConfig.class.getMethod("age", AgeRepository.class), primitiveConfig));

        context.getCache().setComponents(components);
        return context;
    }

    @Test
    public void testBeanAndComponentMapping() throws NoSuchMethodException {
        // Given a context for the test project
        var context = setupContext();

        // When i map the components
        ObjectMapper.mapObjectsAndControllers(context);
        Set<Component> generatedComponents = context.getCache().getComponents();

        // Then there's a DummyController component
        Optional<Component> component = generatedComponents.stream().filter(c->c.getType().equals(DummyController.class)).findFirst();
        Assertions.assertTrue(component.isPresent());
        DummyController dummyController = (DummyController) component.get().getInstance();

        // And it returns the expected value
        Pet pet = dummyController.getPet();
        Assertions.assertEquals(pet, new Pet(new Person("Bartje", 22), "Max"));

        // And there's a controller mapped to /dummy
        Set<MappedController> mappedControllers = context.getCache().getMappedControllers();
        Optional<MappedController> mappedController = mappedControllers.stream().filter(mc->mc.mapping().equals("/dummy")).findFirst();
        Assertions.assertTrue(mappedController.isPresent());

        // And it maps to a DummyController object
        Object controller = mappedController.get().controller();
        Assertions.assertInstanceOf(DummyController.class, controller);

        //      Significance / Tested Mappings:
        //      AgeRepository -> Age = 22               (from AgeRepository default constructor:    Component creation)
        //      String -> Bartje                        (directly from PrimitiveConfig:             Bean creation)
        //      Integer -> 22                           (from AgeRepository dependency:             Component -> Bean injection)
        //      Person -> Age = 22, Name = Bartje       (from previous beans:                       Bean -> Bean injection + hierarchical injection + multiple injection types in Bean)
        //      PetNameRepository -> name = Max         (from default constructor)
        //      PetNameService -> name = Max            (from PetNameRepository)                    Component -> Component
        //      Pet -> Person = Bartje, Name = Max      (from Bean + PetNameService)                Component -> Component, Bean -> Component, multiple injection types in Component)
    }

}
