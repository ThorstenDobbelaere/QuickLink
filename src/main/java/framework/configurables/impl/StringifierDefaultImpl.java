package framework.configurables.impl;

import framework.configurables.Stringifier;

class StringifierDefaultImpl implements Stringifier {
    @Override
    public String stringify(Object o) {
        return o.toString();
    }
}
