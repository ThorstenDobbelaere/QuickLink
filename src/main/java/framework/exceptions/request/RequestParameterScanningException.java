package framework.exceptions.request;

import java.util.List;

public class RequestParameterScanningException extends RuntimeException {
    private RequestParameterScanningException(String message) {
        super(message);
    }

    private RequestParameterScanningException(String description, List<Class<?>> expectedClasses) {
        this(String.format("%s (expecting types %s)", description, expectedClasses));
    }

    public static RequestParameterScanningException parseException(String url, int index, List<Class<?>> expectedClasses) {
        return new RequestParameterScanningException(String.format("Parsing format error in URL %s, index %d", url, index), expectedClasses);
    }

    public static RequestParameterScanningException tooFewArgumentsException(String url, List<Class<?>> expectedClasses) {
        return new RequestParameterScanningException(String.format("Too few arguments in URL %s", url), expectedClasses);
    }

    public static RequestParameterScanningException tooManyArgumentsException(String url, List<Class<?>> expectedClasses) {
        return new RequestParameterScanningException(String.format("Too many arguments in URL %s", url), expectedClasses);
    }

    public static RequestParameterScanningException unsupportedType(Class<?> expectedClass) {
        return new RequestParameterScanningException(String.format("Unsupported type %s", expectedClass));
    }
}
