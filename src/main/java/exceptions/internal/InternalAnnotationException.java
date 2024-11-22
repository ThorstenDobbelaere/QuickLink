package exceptions.internal;

public class InternalAnnotationException extends RuntimeException {
    private InternalAnnotationException(String message) {
        super(message);
    }

    public static InternalAnnotationException expectedAnnotation(Class<?> annotation) {
      return new InternalAnnotationException(String.format("Class %s was expected to be an annotation.", annotation.getName()));
    }

    public static InternalAnnotationException nullAnnotation() {
      return new InternalAnnotationException("Expected annotation, but got null.");
    }
}
