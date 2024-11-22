package exceptions;

import annotations.clarification.PrimaryConstructor;

public class ConstructorScanException extends RuntimeException {
    private ConstructorScanException(String description) {
        super(description);
    }

    public static ConstructorScanException multipleConstructorsNoPrimary(Class<?> type) {
        return new ConstructorScanException(String.format("Multiple constructors found for type %s. Please annotate one with @%s to resolve ambiguity.", type.getName(), PrimaryConstructor.class.getName()));
    }

    public static ConstructorScanException noConstructor(Class<?> type) {
        return new ConstructorScanException(String.format("No constructor available for type %s. Please provide a non-private constructor for dependency injection.", type.getName()));
    }

    public static ConstructorScanException multiplePrimaryConstructors(Class<?> type) {
        return new ConstructorScanException(String.format("Multiple primary constructors found for type %s. Please annotate only 1 with @%s.", type.getName(), PrimaryConstructor.class.getName()));
    }

    public static ConstructorScanException configNoDefaultConstructor(Class<?> type) {
        return new ConstructorScanException(String.format("Could not find default constructor for Configuration class %s.", type.getName()));
    }

}
