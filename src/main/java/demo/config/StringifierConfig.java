package demo.config;

import demo.config.types.JsonStringifier;
import framework.annotations.injection.config.Bean;
import framework.annotations.injection.config.Config;

@Config
public class StringifierConfig {

    @Bean
    public JsonStringifier jsonStringifier() {
        return new JsonStringifier();
    }
}
