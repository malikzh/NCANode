package kz.ncanode.ioc.exceptions;

public class CircularDependencyException extends Exception {

    public CircularDependencyException(String message) {
        super(message);
    }
}
