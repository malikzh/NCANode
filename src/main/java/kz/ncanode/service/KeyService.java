package kz.ncanode.service;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.ncanode.constants.MessageConstants;
import kz.ncanode.dto.request.SignerRequest;
import kz.ncanode.exception.KeyException;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyService {
    private final KalkanProvider kalkanProvider;

    /**
     * Читает ключ p12
     *
     * @param key Key in Base64 format
     * @param password Password
     * @return Ключ ЭЦП
     */
    public KeyStore read(String key, String password) throws KeyStoreException, KeyException {
        KeyStore store = KeyStore.getInstance("PKCS12", kalkanProvider);

        byte[] decodedKey;

        try {
            decodedKey = Base64.getDecoder().decode(key);
        } catch (Exception e) {
            log.error(MessageConstants.KEY_INVALID_BASE64, e);
            throw new KeyException(MessageConstants.KEY_INVALID_BASE64, e);
        }


        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedKey)) {
            store.load(inputStream, password.toCharArray());
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            final String message = createMessageFromException(e);
            log.error(message, e);
            throw new KeyException(message, e);
        }

        return store;
    }

    public List<KeyStore> read(final List<SignerRequest> signers) {
        return IntStream.range(0, signers.size())
            .mapToObj(i -> tryReadKey(signers, i))
            .collect(Collectors.toList());
    }

    private KeyStore tryReadKey(List<SignerRequest> signers, Integer index) {
        return null; //todo
    }

    private String createMessageFromException(Exception e) {
        return switch (e.getMessage()) {
            case "stream does not represent a PKCS12 key store" -> MessageConstants.KEY_INVALID_FORMAT;
            case "PKCS12 key store mac invalid - wrong password or corrupted file." -> MessageConstants.KEY_INVALID_PASSWORD;
            default -> MessageConstants.KEY_UNKNOWN_ERROR;
        };
    }
}
