package kz.ncanode.ioc.exceptions;

public class ServiceProviderAlreadyExistsException extends Exception {
    public ServiceProviderAlreadyExistsException () {
        super();
    }

    public ServiceProviderAlreadyExistsException(String message) {
        super(message);
    }
}
