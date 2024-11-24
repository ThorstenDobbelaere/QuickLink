package demo.config;

import framework.annotations.injection.config.Bean;
import framework.annotations.injection.config.Config;
import demo.controller.TestController;

import java.util.List;

@Config
public class TestConfig {

    @Bean
    protected String helloWorld(TestController controller) {
        return controller.test();
    }

    @Bean
    protected List<Integer> integerList(){
        return List.of(1, 2, 3, 4, 5);
    }
}
