package demo.repository;

import demo.model.Animal;
import framework.annotations.injection.semantic.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class AnimalRepository {
    private Set<Animal> animals;

    public AnimalRepository() {
        animals = new HashSet<>();
        animals.add(new Animal("Dog", "Woof"));
        animals.add(new Animal("Cat", "Meow"));
    }

    public Set<Animal> getAnimals() {
        return animals;
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }
}
