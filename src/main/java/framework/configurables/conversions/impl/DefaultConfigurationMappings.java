package framework.configurables.conversions.impl;

import framework.configurables.conversions.OutputConverter;

public class DefaultConfigurationMappings {
    public OutputConverter outputConverter(){
        return new OutputConverterDefaultImpl();
    }
}
