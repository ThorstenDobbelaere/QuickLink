package framework.configurables;

import framework.http.responseentity.ContentType;

public interface Stringifier {
    String stringify(Object o);
    ContentType getContentType();
}
