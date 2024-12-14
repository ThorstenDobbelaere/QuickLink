package testprojects.testproject.controller;

import framework.annotations.injection.semantic.Controller;
import framework.annotations.mapping.OutputMapping;
import testprojects.testproject.model.Person;
import testprojects.testproject.model.Pet;
import testprojects.testproject.service.PetNameService;

@Controller("/dummy")
public class DummyController {
    private final PetNameService petNameService;
    private final Person petOwner;

    public DummyController(PetNameService petNameService, Person petOwner) {
        this.petNameService = petNameService;
        this.petOwner = petOwner;
    }

    @OutputMapping("/pet")
    public Pet getPet(){
        return new Pet(petOwner, petNameService.getPetName());
    }


}
