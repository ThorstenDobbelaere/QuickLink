package testprojects.testproject.service;

import component_scan.annotations.injection.semantic.Service;
import component_scan.annotations.interception.Timed;
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
