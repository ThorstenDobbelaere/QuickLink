package framework.exceptions.internal;

public class MapMethodObjectInternalError extends RuntimeException {
    private MapMethodObjectInternalError(String message) {
        super(message);
    }

    public static MapMethodObjectInternalError configNotFound(Class<?> configClass) {
        return new MapMethodObjectInternalError(String.format("Unable to find configuration class '%s' in mappings.", configClass.getName()));
    }
}
