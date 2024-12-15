package demo.config;

import demo.config.output.ColoredOutputConverter;
import demo.config.output.JsonOutputConverter;
import framework.annotations.injection.config.Bean;
import framework.annotations.injection.config.Config;
import framework.configurables.conversions.OutputConverter;
import framework.configurables.conversions.impl.OutputConverterDefaultImpl;

@Config
public class OutputConverterConfig {
    private static boolean isConsole = false;

    public static void setIsConsole(boolean isConsole) {
        OutputConverterConfig.isConsole = isConsole;
    }

    // Color the outputs yellow when in console mode
    @Bean
    public OutputConverter outputConverter() {
        if (isConsole) {
            return new ColoredOutputConverter();
        }
        return new OutputConverterDefaultImpl();
    }

    @Bean
    public JsonOutputConverter jsonOutputConverter() {
        return new JsonOutputConverter();
    }
}
