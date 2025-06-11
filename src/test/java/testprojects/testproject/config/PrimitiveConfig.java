package testprojects.testproject.config;

import component_scan.annotations.injection.config.Bean;
import component_scan.annotations.injection.config.Config;
import testprojects.testproject.repo.AgeRepository;

@Config
public class PrimitiveConfig {

    @Bean
    public String ownerName(){
        return "Bartje";
    }

    @Bean
    public Integer age(AgeRepository ageRepository){
        return ageRepository.getAge();
    }
}
