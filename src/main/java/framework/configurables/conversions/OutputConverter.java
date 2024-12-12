package framework.configurables.conversions;

import framework.request.response.ContentType;

public interface OutputConverter {
    String stringify(Object o);
    ContentType getContentType();
}
