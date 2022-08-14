package kz.ncanode.util;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.List;

public class KeyUtil {

    /**
     * Преобразует алиасы в список
     * @param key ЭЦП
     * @return Список алиасов
     */
    public static List<String> getAliases(KeyStore key) {
        var list = new ArrayList<String>();

        try {
            var aliases = key.aliases();

            while (aliases.hasMoreElements()) {
                list.add(aliases.nextElement());
            }
        } catch (KeyStoreException ignored) {

        }

        return list;
    }
}
