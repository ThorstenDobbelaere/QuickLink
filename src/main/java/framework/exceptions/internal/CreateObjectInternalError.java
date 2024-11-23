package framework.exceptions.internal;

import java.lang.reflect.InvocationTargetException;

public class CreateObjectInternalError extends RuntimeException {
    private CreateObjectInternalError(String message) {
        super(message);
    }

    public static CreateObjectInternalError wrongArgCount(Class<?> type) {
        return new CreateObjectInternalError(String.format("Could not create instance for %s: Wrong number of arguments.", type.getName()));
    }

    public static CreateObjectInternalError wrongArgTypes(Class<?> type) {
        return new CreateObjectInternalError(String.format("Could not create instance for %s: Incompatible argument types.", type.getName()));
    }

    public static CreateObjectInternalError noAccess(Class<?> type) {
        return new CreateObjectInternalError(String.format("Could not create instance for %s: Unable to access method or constructor.", type.getName()));
    }

    public static CreateObjectInternalError invocationException(Class<?> type, InvocationTargetException e) {
        return new CreateObjectInternalError(String.format("Could not create instance for %s: Exception thrown by method or constructor: %s.", type.getName(), e.getMessage()));
    }

    public static CreateObjectInternalError newInstanceException(Class<?> type, InstantiationException e) {
        return new CreateObjectInternalError(String.format("Could not create instance for %s: Constructor not available: %s.", type.getName(), e.getMessage()));
    }
}
