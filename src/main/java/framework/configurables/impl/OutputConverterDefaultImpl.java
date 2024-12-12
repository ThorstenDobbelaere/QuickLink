package framework.configurables.impl;

import framework.configurables.OutputConverter;
import framework.request.response.ContentType;

public class OutputConverterDefaultImpl implements OutputConverter {
    @Override
    public String stringify(Object o) {
        return o.toString();
    }

    @Override
    public ContentType getContentType() {
        return ContentType.PLAIN;
    }
}
