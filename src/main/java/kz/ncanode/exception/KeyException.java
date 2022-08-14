package kz.ncanode.exception;

public class KeyException extends Exception {
    public KeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyException(String message) {
        super(message);
    }
}
