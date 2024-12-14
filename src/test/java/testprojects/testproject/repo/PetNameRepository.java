package testprojects.testproject.repo;

import framework.annotations.injection.semantic.Repository;

@Repository
public class PetNameRepository {
    private final String petName;

    public PetNameRepository() {
        this.petName = "Max";
    }

    public String getPetName() {
        return petName;
    }
}
