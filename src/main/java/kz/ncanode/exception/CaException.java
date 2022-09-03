package kz.ncanode.exception;

public class CaException extends RuntimeException {
    public CaException(String message) {
        super(message);
    }

    public CaException(String message, Throwable cause) {
        super(message, cause);
    }
}
