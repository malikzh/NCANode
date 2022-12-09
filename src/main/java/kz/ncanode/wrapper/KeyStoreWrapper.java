package kz.ncanode.wrapper;

import kz.ncanode.constants.MessageConstants;
import kz.ncanode.exception.ServerException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.security.*;
import java.security.cert.X509Certificate;
import java.util.List;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class KeyStoreWrapper {
    private final KeyStore keyStore;
    private final String alias;
    private final String password;
    private final List<String> aliases;

    /**
     * Извлекает приватный ключ.
     * @return Приватный ключ
     */
    public PrivateKey getPrivateKey() {
        try {
            return (PrivateKey)getKeyStore().getKey(getAlias(), getPassword().toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            log.error(MessageConstants.KEY_CANT_EXTRACT_PRIVATE_KEY, e);
            throw new ServerException(MessageConstants.KEY_CANT_EXTRACT_PRIVATE_KEY, e);
        }
    }

    /**
     * Извлекает сертификат из Signer.
     *
     * @return X509 Certificate
     */
    public CertificateWrapper getCertificate() {
        try {
            return new CertificateWrapper(
                (X509Certificate)getKeyStore().getCertificate(getAlias())
            );
        } catch (KeyStoreException e) {
            log.error(MessageConstants.KEY_CANT_EXTRACT_CERTIFICATE, e);
            throw new ServerException(MessageConstants.KEY_CANT_EXTRACT_CERTIFICATE, e);
        }
    }
}
