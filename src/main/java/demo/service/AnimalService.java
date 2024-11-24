package demo.service;

import demo.model.Animal;
import demo.repository.AnimalRepository;
import framework.annotations.injection.semantic.Service;
import framework.annotations.interception.Timed;

import java.util.List;

@Service
public class AnimalService {
    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Timed
    public List<Animal> getAnimals(){
        return animalRepository.getAnimals().stream().toList();
    }

    @Timed
    public void addAnimal(Animal animal){
        animalRepository.addAnimal(animal);
    }
}
