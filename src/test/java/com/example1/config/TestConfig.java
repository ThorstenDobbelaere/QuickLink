package com.example1.config;

import annotations.injection.config.Bean;
import annotations.injection.config.Config;
import com.example1.controller.TestController;

import java.util.List;

@Config
public class TestConfig {

    @Bean
    protected String helloWorld(final TestController controller) {
        return controller.test();
    }

    @Bean
    protected List<Integer> integerList(){
        return List.of(1, 2, 3, 4, 5);
    }
}
