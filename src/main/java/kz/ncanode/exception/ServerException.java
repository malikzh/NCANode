package kz.ncanode.exception;

import org.springframework.http.HttpStatus;

/**
 * Исключения возникающие по причине сервера
 */
public class ServerException extends ExceptionBase {
    public ServerException(String message) {
        super(message);
    }

    @Override
    Integer getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
