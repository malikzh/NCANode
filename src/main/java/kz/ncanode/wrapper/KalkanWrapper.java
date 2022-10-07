package kz.ncanode.wrapper;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.ncanode.constants.MessageConstants;
import kz.ncanode.dto.request.SignerRequest;
import kz.ncanode.exception.KeyException;
import kz.ncanode.exception.ServerException;
import kz.ncanode.util.KeyUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
@Component
@RequiredArgsConstructor
public class KalkanWrapper {
    @Getter
    private final KalkanProvider kalkanProvider;

    /**
     * Читает ключ P12.
     *
     * @param key Ключ в формате Base64
     * @param keyAlias Алиас ключа. Может быть null. Тогда выберется самый первый алиас в ключе.
     * @param password Пароль к ключу
     * @return Открытое хранилище ключей
     * @throws KeyException Если произошла ошибка при открытии ключа
     */
    public KeyStoreWrapper read(String key, String keyAlias, String password) throws KeyException {
        KeyStore store;

        try {
            store = KeyStore.getInstance("PKCS12", kalkanProvider);
        } catch (KeyStoreException e) {
            log.error(MessageConstants.KEY_ENGINE_ERROR, e);
            throw new KeyException(MessageConstants.KEY_ENGINE_ERROR, e);
        }

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

        // Checking for alias
        var aliases = KeyUtil.getAliases(store);

        if (aliases.isEmpty()) {
            log.error(MessageConstants.KEY_ALIASES_NOT_FOUND);
            throw new KeyException(MessageConstants.KEY_ALIASES_NOT_FOUND);
        }

        if (keyAlias != null && !aliases.contains(keyAlias)) {
            final String err = String.format(MessageConstants.KEY_ALIAS_NOT_FOUND, keyAlias);
            log.error(err);
            throw new KeyException(err);
        } else {
            keyAlias = aliases.get(0);
        }

        return new KeyStoreWrapper(store, keyAlias, password, aliases);
    }

    /**
     * Читает ключи из запроса SignerRequest.
     *
     * @param signers Ключи из запроса в формате Base64
     * @return Прочитанные ключи
     */
    public List<KeyStoreWrapper> read(final List<SignerRequest> signers) {
        return IntStream.range(0, signers.size())
            .mapToObj(i -> tryReadKey(signers, i))
            .collect(Collectors.toList());
    }

    /**
     * Пытается прочитать ключ. Если ничего не получилось, то этот метод формирует подробное описание ошибки.
     *
     * @param signers Ключи из запроса в формате Base64
     * @param index Индекс текущего ключа
     * @return Объект KeyStoreWrapper
     */
    private KeyStoreWrapper tryReadKey(List<SignerRequest> signers, Integer index) {
        SignerRequest signerRequest = signers.get(index);

        try {
            return read(signerRequest.getKey(), signerRequest.getKeyAlias(), signerRequest.getPassword());
        } catch (KeyException e) {
            final String errorMessage = String.format("signers[%d]: %s", index, e.getMessage());
            log.error(errorMessage, e.getCause());
            throw new ServerException(errorMessage, e.getCause());
        }
    }

    /**
     * Данный метод преобразует текст ошибки из KalkanCrypt в наш текст. Это сделано для того,
     * чтобы в ответ сервера не записать ничего лишнего, а только ошибку.
     *
     * @param e Exception
     * @return Текст ошибки
     */
    private String createMessageFromException(Exception e) {
        return switch (e.getMessage()) {
            case "stream does not represent a PKCS12 key store" -> MessageConstants.KEY_INVALID_FORMAT;
            case "PKCS12 key store mac invalid - wrong password or corrupted file." -> MessageConstants.KEY_INVALID_PASSWORD;
            default -> MessageConstants.KEY_UNKNOWN_ERROR;
        };
    }
}
