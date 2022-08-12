package kz.ncanode.exception;

import org.springframework.http.HttpStatus;

/**
 * Исключения возникающие по ошибке клиента
 */
public class ClientException extends ExceptionBase {
    public ClientException(String message) {
        super(message);
    }

    @Override
    Integer getStatus() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
