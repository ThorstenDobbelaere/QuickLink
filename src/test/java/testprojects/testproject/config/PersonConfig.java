package testprojects.testproject.config;

import framework.annotations.injection.config.Bean;
import framework.annotations.injection.config.Config;
import testprojects.testproject.model.Person;

@Config
public class PersonConfig {

    @Bean
    public Person createPerson(String name, Integer age){
        return new Person(name, age);
    }
}
