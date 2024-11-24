package testproject.repo;

import framework.annotations.injection.semantic.Repository;

@Repository
public class AgeRepository {
    private final int age;

    public AgeRepository() {
        this.age = 22;
    }

    public int getAge() {
        return age;
    }
}
