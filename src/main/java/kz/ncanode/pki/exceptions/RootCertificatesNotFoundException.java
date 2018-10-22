package kz.ncanode.pki.exceptions;

public class RootCertificatesNotFoundException extends Exception {
    public RootCertificatesNotFoundException(String message) {
        super(message);
    }

    public RootCertificatesNotFoundException() {
        super();
    }
}
