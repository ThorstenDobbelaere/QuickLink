package framework.configurables;

import framework.request.response.ContentType;

public interface Stringifier {
    String stringify(Object o);
    ContentType getContentType();
}
