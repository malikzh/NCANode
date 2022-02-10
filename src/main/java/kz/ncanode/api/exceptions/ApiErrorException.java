package kz.ncanode.api.exceptions;

import kz.ncanode.api.core.ApiStatus;

import java.net.HttpURLConnection;

public class ApiErrorException extends Exception {
    protected int httpCode;

    protected int status = ApiStatus.STATUS_API_ERROR;

    public ApiErrorException(String message) {
        super(message);

        this.httpCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
    }

    public ApiErrorException(String message, int httpCode, int status) {
        super(message);

        this.httpCode = httpCode;
        this.status = status;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public int getStatus() {
        return status;
    }

    public ApiErrorException() {
        super();
    }
}
