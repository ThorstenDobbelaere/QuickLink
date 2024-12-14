package framework.request.response;

import framework.configurables.conversions.OutputConverter;

public class HttpResponse {
    private final HttpStatus status;
    private final ContentType contentType;
    private final String body;

    public HttpResponse(ResponseEntity entity, OutputConverter outputConverter) {
        this.status = entity.status();
        this.contentType = outputConverter.getContentType();
        this.body = outputConverter.stringify(entity.data());
    }

    public HttpResponse(HttpStatus status) {
        this.status = status;
        this.contentType = ContentType.PLAIN;
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
