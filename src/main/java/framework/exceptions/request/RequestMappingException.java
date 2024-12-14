package framework.exceptions.request;

import java.lang.annotation.Annotation;

public class RequestMappingException extends RuntimeException {
    public RequestMappingException(String message) {
        super(message);
    }

    public static RequestMappingException unsupportedMethodAnnotation(Class<? extends Annotation> methodAnnotation) {
        return new RequestMappingException(String.format("Unsupported method annotation: %s", methodAnnotation.getSimpleName()));
    }
}
