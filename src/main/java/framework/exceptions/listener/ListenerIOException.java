package framework.exceptions.listener;

import java.io.IOException;

public class ListenerIOException extends RuntimeException {
    private ListenerIOException(String message) {
        super(message);
    }

    public static ListenerIOException of(IOException exception) {
        return new ListenerIOException("An I/O error occurred in the listener: " + exception.getMessage());
    }
}
