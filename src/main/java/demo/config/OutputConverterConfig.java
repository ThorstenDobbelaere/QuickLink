package demo.config;

import demo.config.types.JsonOutputConverter;
import framework.annotations.injection.config.Bean;
import framework.annotations.injection.config.Config;

@Config
public class OutputConverterConfig {

    @Bean
    public JsonOutputConverter jsonOutputConverter() {
        return new JsonOutputConverter();
    }
}
