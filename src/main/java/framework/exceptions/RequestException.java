package framework.exceptions;

public class RequestException extends RuntimeException {
    private RequestException(String message) {
        super(message);
    }


    public static RequestException noStringExpected(String mapping, String input) {
      return new RequestException(String.format("No input expected at mapping '%s', got parameter '%s'", mapping, input));
    }

    public static RequestException invokeException(Exception e) {
      return new RequestException(String.format("Invoke exception: %s", e.getMessage()));
    }
}
