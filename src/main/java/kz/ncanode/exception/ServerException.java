package kz.ncanode.exception;

import org.springframework.http.HttpStatus;

/**
 * Исключения возникающие по причине сервера
 */
public class ServerException extends ApplicationException {
    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public Integer getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
