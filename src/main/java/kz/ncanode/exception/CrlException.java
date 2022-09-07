package kz.ncanode.exception;

public class CrlException extends RuntimeException {
    public CrlException(String message) {
        super(message);
    }

    public CrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
