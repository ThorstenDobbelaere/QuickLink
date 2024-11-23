package framework.exceptions.request;

public class RequestMappingException extends RuntimeException {
    public RequestMappingException(String message) {
        super(message);
    }
}
