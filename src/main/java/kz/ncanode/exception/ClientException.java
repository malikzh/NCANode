package kz.ncanode.exception;

import org.springframework.http.HttpStatus;

/**
 * Исключения возникающие по ошибке клиента
 */
public class ClientException extends ApplicationException {
    public ClientException(String message) {
        super(message);
    }
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public Integer getStatus() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
