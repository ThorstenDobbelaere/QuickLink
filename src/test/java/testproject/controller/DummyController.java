package testproject.controller;

import framework.annotations.injection.semantic.Controller;
import testproject.model.Person;
import testproject.model.Pet;
import testproject.service.PetNameService;

@Controller("/dummy")
public class DummyController {
    private final PetNameService petNameService;
    private final Person petOwner;

    public DummyController(PetNameService petNameService, Person petOwner) {
        this.petNameService = petNameService;
        this.petOwner = petOwner;
    }

    public Pet getPet(){
        return new Pet(petOwner, petNameService.getPetName());
    }
}
