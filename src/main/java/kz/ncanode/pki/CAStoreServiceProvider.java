package kz.ncanode.pki;

import kz.ncanode.Helper;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import kz.ncanode.pki.exceptions.RootCertificatesNotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

/**
 * Класс, который хранит в себе промежуточные (trusted) и корневые сертификаты (root)
 * Также, здесь находится реализация алгоритма проверки цепочки. Важно учесть,
 * что notBefore и notAfter здесь не проверяются.
 */
public class CAStoreServiceProvider implements ServiceProvider {
    public final static String CERT_FILE_EXT = ".crt";

    private ArrayList<X509Certificate> root = new ArrayList<>();
    private ArrayList<X509Certificate> trusted = new ArrayList<>();

    private ConfigServiceProvider   config = null;
    private OutLogServiceProvider   out    = null;
    private ErrorLogServiceProvider err    = null;

    public CAStoreServiceProvider(ConfigServiceProvider config, OutLogServiceProvider out, ErrorLogServiceProvider err) {
        this.config = config;
        this.out    = out;
        this.err    = err;

        String rootCaDir    = Helper.absolutePath(config.get("ca", "root_dir"));
        String trustedCaDir = Helper.absolutePath(config.get("ca", "trusted_dir"));

        int count = 0;

        // Load root certificates
        this.out.write("Loading root certificates from: " + rootCaDir);
        count = load(rootCaDir, root);
        this.out.write("Loaded " + count + " certificates");


        // Load trusted certificates
        this.out.write("Loading trusted certificates from: " + trustedCaDir);
        count = load(trustedCaDir, trusted);
        this.out.write("Loaded " + count + " certificates");
    }

    protected int load(String path, ArrayList<X509Certificate> store) {
        File rootCaFiles = new File(path);

        int counter = 0;

        File[] files = rootCaFiles.listFiles();

        if (files == null) {
            err.write("WARNING! Cannot load certificates from directory: " + path);
            return 0;
        }

        for (File cert : files) {
            String p = cert.getAbsolutePath();
            if (!Helper.fileExt(cert).equals(CERT_FILE_EXT)) continue;

            out.write("Loading certificate: " + p);
            try {
                X509Certificate x509 = X509Manager.load(p);
                ++counter;
                store.add(x509);
            } catch (CertificateException e) {
                err.write("Cannot open load certificate: " + p + ". Exception: " + e.getMessage());
            } catch (NoSuchProviderException e) {
                err.write("Cannot open load certificate: " + p + ". Exception: " + e.getMessage());
            } catch (FileNotFoundException e) {
                err.write("Cannot open load certificate: " + p + ". Exception: " + e.getMessage());
            } catch (IOException e) {
                err.write("Cannot open load certificate: " + p + ". Exception: " + e.getMessage());
            }
        }

        return counter;
    }

    public ArrayList<X509Certificate> chain(X509Certificate cert) throws NoSuchAlgorithmException, CertificateException, NoSuchProviderException, InvalidKeyException, SignatureException, RootCertificatesNotFoundException {
        ArrayList<X509Certificate> result = new ArrayList<>();

        if (root.isEmpty()) {
            throw new RootCertificatesNotFoundException("Root certificates not found.");
        }

        // Добавляем проверяемый сертификат в начало цепочки
        result.add(cert);

        // Проверяем все доверенные (промежуточные) сертификаты
        X509Certificate c = null;

        while ((c = chainTrusted(result.get(result.size() - 1))) != null) {
            if (!result.contains(c)) {
                result.add(c);
            }
        }

        // Если хотя бы нашли один корневой сертификат, то всё, цепочка заслуживает доверия
        if ((c = chainRoot(result.get(result.size() - 1))) != null) {
            result.add(c);
            return result;
        }

        return null;
    }

    protected X509Certificate chainRoot(X509Certificate cert) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        // Если нам подсунут корневой сертификат, для построения цепочки, то без этой проверки, алгоритм уйдет в бесконечный цикл
        if (cert.getIssuerDN().equals(cert.getSubjectDN())) return null;

        for (X509Certificate item : root) {
            if (cert.getIssuerDN().equals(item.getSubjectDN())) {
                cert.verify(item.getPublicKey());
                return item;
            }
        }

        return null;
    }

    protected X509Certificate chainTrusted(X509Certificate cert) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        if (cert.getIssuerDN().equals(cert.getSubjectDN())) return null;

        for (X509Certificate item : trusted) {
            if (cert.getIssuerDN().equals(item.getSubjectDN())) {
                cert.verify(item.getPublicKey());
                return item;
            }
        }

        return null;
    }
}
