package kz.ncanode.service;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyService {
    private final KalkanProvider kalkanProvider;

    /**
     * Открывает ключ p12
     *
     * @param key Key in Base64 format
     * @param password Password
     * @return Ключ ЭЦП
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     */
    public KeyStore load(String key, String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore store = KeyStore.getInstance("PKCS12", kalkanProvider);

        var decodedKey = Base64.getDecoder().decode(key);

        try(ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedKey)) {
            store.load(inputStream, password.toCharArray());
        }

        return store;
    }
}
