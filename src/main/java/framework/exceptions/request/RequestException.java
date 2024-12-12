package framework.exceptions.request;

public class RequestException extends RuntimeException {
    private RequestException(String message) {
        super(message);
    }

    public static RequestException requestInvoked(Throwable e) {
      return new RequestException(String.format("Invoke exception: %s", e.getMessage()));
    }
}
