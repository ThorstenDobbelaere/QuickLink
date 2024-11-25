package framework.http.internal;

import framework.configurables.Stringifier;
import framework.http.responseentity.ContentType;
import framework.http.responseentity.HttpStatus;
import framework.http.responseentity.ResponseEntity;

public class HttpResponse {
    private final HttpStatus status;
    private final ContentType contentType;
    private final String body;

    public HttpResponse(ResponseEntity entity, Stringifier stringifier) {
        this.status = entity.status();
        this.contentType = stringifier.getContentType();
        this.body = stringifier.stringify(entity.data());
    }

    public HttpResponse(HttpStatus status) {
        this.status = status;
        this.contentType = ContentType.JSON;
        this.body = "";
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }
}
