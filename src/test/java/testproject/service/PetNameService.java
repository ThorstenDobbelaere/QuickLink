package testproject.service;

import testproject.repo.PetNameRepository;

public class PetNameService {
    private final PetNameRepository repo;
    public PetNameService(PetNameRepository repo) {
        this.repo = repo;
    }

    public String getPetName(){
        return repo.getPetName();
    }
}
