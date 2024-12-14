package framework.exceptions.wrapper;

import framework.request.response.HttpStatus;

public class HttpException extends RuntimeException {
    private final HttpStatus status;

    public HttpException(Exception exception) {
        super(exception);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpException(Exception exception, HttpStatus status) {
        super(exception);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
