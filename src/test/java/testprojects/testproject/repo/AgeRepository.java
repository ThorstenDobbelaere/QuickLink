package testprojects.testproject.repo;

import component_scan.annotations.injection.semantic.Repository;
import component_scan.annotations.interception.Timed;

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
