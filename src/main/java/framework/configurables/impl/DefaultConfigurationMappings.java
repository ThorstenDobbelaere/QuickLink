package framework.configurables.impl;

import framework.configurables.Stringifier;

public class DefaultConfigurationMappings {
    public Stringifier stringifier(){
        return new StringifierDefaultImpl();
    }
}
