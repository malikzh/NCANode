package kz.ncanode.exception;

public class CrlException extends Exception{
    public CrlException(String message) {
        super(message);
    }

    public CrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
