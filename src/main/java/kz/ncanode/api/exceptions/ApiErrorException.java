package kz.ncanode.api.exceptions;

import java.net.HttpURLConnection;

public class ApiErrorException extends Exception {
    protected int httpCode;

    public ApiErrorException(String message) {
        super(message);

        this.httpCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
    }

    public ApiErrorException(String message, int httpCode) {
        super(message);

        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public ApiErrorException() {
        super();
    }
}
