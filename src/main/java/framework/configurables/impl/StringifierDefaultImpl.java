package framework.configurables.impl;

import framework.configurables.Stringifier;
import framework.http.responseentity.ContentType;

public class StringifierDefaultImpl implements Stringifier {
    @Override
    public String stringify(Object o) {
        return o.toString();
    }

    @Override
    public ContentType getContentType() {
        return ContentType.PLAIN;
    }
}
