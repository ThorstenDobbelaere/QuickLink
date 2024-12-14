package framework.exceptions.componentscan;

public class MappingException extends RuntimeException {
    private MappingException(String message) {
        super(message);
    }

    public static MappingException emptyMapping() {
        return new MappingException("The project contains a method with an empty mapping");
    }
}
