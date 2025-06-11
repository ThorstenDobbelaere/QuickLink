package testprojects.testproject.config;

import component_scan.annotations.injection.config.Bean;
import component_scan.annotations.injection.config.Config;
import testprojects.testproject.model.Person;

@Config
public class PersonConfig {

    @Bean
    public Person createPerson(String name, Integer age){
        return new Person(name, age);
    }
}
