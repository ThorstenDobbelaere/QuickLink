package framework.configurables.impl;

import framework.configurables.OutputConverter;

public class DefaultConfigurationMappings {
    public OutputConverter outputConverter(){
        return new OutputConverterDefaultImpl();
    }
}
