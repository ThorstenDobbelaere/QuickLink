package testproject.repo;

import framework.annotations.injection.semantic.Repository;
import framework.annotations.interception.Timed;

@Repository
public class AgeRepository {
    private final int age;

    public AgeRepository() {
        this.age = 22;
    }

    @Timed
    public int getAge() {
        return age;
    }
}
