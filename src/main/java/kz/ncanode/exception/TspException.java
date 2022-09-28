package kz.ncanode.exception;

public class TspException extends RuntimeException {
    public TspException(String message) {
        super(message);
    }

    public TspException(String message, Throwable cause) {
        super(message, cause);
    }
}
