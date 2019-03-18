package kz.ncanode.pki;

import kz.ncanode.Helper;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

/**
 * Класс для работы с CRL
 */
public class CrlServiceProvider implements ServiceProvider {

    public static final String CRL_FILE_EXT  = ".crl";

    ConfigServiceProvider config;
    OutLogServiceProvider out;

    public CrlServiceProvider(ConfigServiceProvider config, OutLogServiceProvider out) {
        this.config = config;
        this.out    = out;

        try {
            updateCache(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CrlStatus verify(X509Certificate cert) {

        String[] cfgCrlCacheUrls = config.get("pki", "crl_urls").split(" ");
        Hashtable<String, String> names = crlNames(cfgCrlCacheUrls);

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            for (File crlFile : crls()) {
                FileInputStream fileInputStream = new FileInputStream(crlFile);

                X509CRL crl = (X509CRL)cf.generateCRL(fileInputStream);
                fileInputStream.close();

                if (crl.isRevoked(cert)) {
                    X509CRLEntry entry = crl.getRevokedCertificate(cert);

                    Date revokationDate = null;
                    String revokationReason = "";

                    if (entry != null) {
                        revokationDate   = entry.getRevocationDate();
                        revokationReason = entry.getRevocationReason().toString();
                    }

                    return new CrlStatus(CrlStatus.CrlResult.REVOKED, names.get(crlFile.getName()), revokationDate, revokationReason);
                }

            }

        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (CRLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return new CrlStatus(CrlStatus.CrlResult.ACTIVE, "", null, "");
    }

    public void updateCache(boolean forceUpdate) throws IOException {

        // достаем конфиги
        String cfgCrlCacheDir    = Helper.absolutePath(config.get("pki", "crl_cache_dir"));
        String[] cfgCrlCacheUrls = config.get("pki", "crl_urls").split(" ");
        int cfgCrlCacheLifetime  = Integer.valueOf(config.get("pki", "crl_cache_lifetime"));

        long ct = System.currentTimeMillis();


        Hashtable<String, String> names = crlNames(cfgCrlCacheUrls);

        // Обновляем суещствующие CRL
        if (!forceUpdate) {
            for (File crlFile : crls()) {

                if ((ct - crlFile.lastModified()) > cfgCrlCacheLifetime * 60 * 1000) {
                    String fileName = crlFile.getName();

                    if (names.containsKey(fileName)) {
                        String url = names.get(fileName);
                        String filePath = cfgCrlCacheDir + "/" + fileName;

                        // Загружаем CRL'ки
                        downloadCrl(url, filePath);
                    }
                }
            }
        }

        // Проходимся по урлам, и если CRL'ок в кэше нет, то подтягиваем их
        for (String url : cfgCrlCacheUrls) {
            String fileName = getCrlNameByUrl(url);
            String filePath = cfgCrlCacheDir + "/" + fileName;

            File crlFile = new File(filePath);

            if (!crlFile.exists() || forceUpdate) {
                downloadCrl(url, filePath);
            }
        }
    }

    private ArrayList<File> crls() {
        ArrayList<File> result = new ArrayList<>();

        String cfgCrlCacheDir    = Helper.absolutePath(config.get("pki", "crl_cache_dir"));
        File crlCacheDir = new File(cfgCrlCacheDir);
        File[] files = crlCacheDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!Helper.fileExt(file).equals(CRL_FILE_EXT)) continue;

                result.add(file);
            }
        }

        return result;
    }

    private Hashtable<String, String> crlNames(String[] urls) {
        Hashtable<String, String> result = new Hashtable<>();

        for (String url : urls) {
            result.put(getCrlNameByUrl(url), url);
        }

        return result;
    }

    private String getCrlNameByUrl(String url) {
        return Helper.sha1(url) + ".crl";
    }

    private void downloadCrl(String url, String filePath) throws IOException {

        if (url.equals("") || url.equals(" ")) return;

        out.write("Downloading CRL from: " + url + " ...");
        FileUtils.copyURLToFile(new URL(url), new File(filePath));
    }
}
