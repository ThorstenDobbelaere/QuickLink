package framework.configurables.conversions.impl;

import framework.configurables.conversions.OutputConverter;
import framework.request.response.ContentType;

public class OutputConverterDefaultImpl implements OutputConverter {
    @Override
    public String stringify(Object o) {
        if(o == null) return "";
        return o.toString();
    }

    @Override
    public ContentType getContentType() {
        return ContentType.PLAIN;
    }
}
