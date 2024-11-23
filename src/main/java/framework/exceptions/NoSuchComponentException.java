package framework.exceptions;

public class NoSuchComponentException extends RuntimeException {
    public NoSuchComponentException(Class<?> componentType) {
        super(String.format("No component found for type %s", componentType.getName()));
    }
}
