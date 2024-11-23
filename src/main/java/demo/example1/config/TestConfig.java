package demo.example1.config;

import framework.annotations.injection.config.Bean;
import framework.annotations.injection.config.Config;
import demo.example1.controller.TestController;

import java.util.List;

@Config
public class TestConfig {

    @Bean
    protected String helloWorld(final TestController controller) {
        System.out.println("Running test");
        return controller.test();
    }

    @Bean
    protected List<Integer> integerList(){
        System.out.println("Getting list");
        return List.of(1, 2, 3, 4, 5);
    }
}
