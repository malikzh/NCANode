package kz.ncanode.ioc.exceptions;

public class AmbiguousConstructorException extends Exception {
    public AmbiguousConstructorException() {
        super();
    }

    public AmbiguousConstructorException(String message) {
        super(message);
    }
}
