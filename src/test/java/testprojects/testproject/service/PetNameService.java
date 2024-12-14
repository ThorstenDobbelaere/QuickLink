package testprojects.testproject.service;

import framework.annotations.injection.semantic.Service;
import framework.annotations.interception.Timed;
import testprojects.testproject.repo.PetNameRepository;

@Service
public class PetNameService {
    private final PetNameRepository repo;
    public PetNameService(PetNameRepository repo) {
        this.repo = repo;
    }

    @Timed
    public String getPetName(){
        return repo.getPetName();
    }
}
