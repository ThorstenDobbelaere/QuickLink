package framework.exceptions.internal;

public class ObjectCreationException extends RuntimeException {
    private ObjectCreationException(String message) {
        super(message);
    }

    public static ObjectCreationException invokingDefaultConstructor(Throwable cause) {
        return new ObjectCreationException("Failed to invoke default constructor: " + cause.getMessage());
    }
}
