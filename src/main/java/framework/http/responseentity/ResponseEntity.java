package framework.http.responseentity;

import framework.configurables.Stringifier;

public record ResponseEntity (Object data, ContentType contentType, HttpStatus status, Stringifier stringifier) {
}
