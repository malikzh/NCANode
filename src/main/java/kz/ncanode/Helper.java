package kz.ncanode;

import kz.gov.pki.kalkan.asn1.ASN1InputStream;
import kz.gov.pki.kalkan.asn1.DERObject;
import kz.gov.pki.kalkan.asn1.pkcs.PKCSObjectIdentifiers;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedDataGenerator;
import kz.gov.pki.kalkan.tsp.TSPAlgorithms;
import org.apache.xml.security.encryption.XMLCipherParameters;
import org.apache.xml.security.utils.Constants;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Класс-хелпер. Здесь лежат всякие методы, которые понадобились для работы других модулей
 */
public class Helper {
    public static String absolutePath(String path) {
        return Paths.get(path).toAbsolutePath().toString();
    }

    public static String fileExt(File file) {
        String ext = "";

        try {
            String name = file.getName();
            ext = name.substring(name.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            //
        }

        return ext;
    }

    public static String dateTime(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static String sha1(String data)
    {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(data.getBytes("UTF-8"));
            sha1 = bytesToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return sha1;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getHashingAlgorithmByOID(String oid) {
        HashMap<String, String> algos = new HashMap<>();

        algos.put(TSPAlgorithms.MD5,"MD5");
        algos.put(TSPAlgorithms.SHA1,"SHA1");
        algos.put(TSPAlgorithms.SHA224,"SHA224");
        algos.put(TSPAlgorithms.SHA256,"SHA256");
        algos.put(TSPAlgorithms.SHA384,"SHA384");
        algos.put(TSPAlgorithms.SHA512,"SHA512");
        algos.put(TSPAlgorithms.RIPEMD128,"RIPEMD128");
        algos.put(TSPAlgorithms.RIPEMD160,"RIPEMD160");
        algos.put(TSPAlgorithms.RIPEMD256,"RIPEMD256");
        algos.put(TSPAlgorithms.GOST34311GT,"GOST34311GT");
        algos.put(TSPAlgorithms.GOST34311,"GOST34311");

        return algos.get(oid);
    }

    /**
     * Метод возвращает алгоритм подписи по OID
     *
     * @param oid OID
     * @return Массив с двумя элементами (Первый = Алгоритм подписи, второй = Алгоритм хэширования)
     */
    public static String[] getSignMethodByOID(String oid) {

        String ret[] = new String[2];


        if (oid.equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
            ret[0] = Constants.MoreAlgorithmsSpecNS + "rsa-sha1";
            ret[1] = Constants.MoreAlgorithmsSpecNS + "sha1";
        } else if (oid.equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
            ret[0] = Constants.MoreAlgorithmsSpecNS + "rsa-sha256";
            ret[1] = XMLCipherParameters.SHA256;
        } else {
            ret[0] = Constants.MoreAlgorithmsSpecNS + "gost34310-gost34311";
            ret[1] = Constants.MoreAlgorithmsSpecNS + "gost34311";
        }

        return ret;
    }

    /**
     * Возвращает алгоритм хэширования по алгоритму подписи
     *
     * @param signOid
     * @return
     */
    public static String getDigestAlgorithmOidBYSignAlgorithmOid(String signOid) {
        if (signOid.equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
            return CMSSignedDataGenerator.DIGEST_SHA1;
        }
        else if (signOid.equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
            return CMSSignedDataGenerator.DIGEST_SHA256;
        }
        else {
            return CMSSignedDataGenerator.DIGEST_GOST34311_95;
        }
    }

    /**
     * Возвращает алгоритм подписи для TSP
     */

    public static String getTspHashAlgorithmByOid(String signOid) {
        if (signOid.equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
            return TSPAlgorithms.SHA1;
        }
        else if (signOid.equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
            return TSPAlgorithms.SHA256;
        }
        else {
            return TSPAlgorithms.GOST34311;
        }
    }

    public static DERObject byteToASN1(byte[] data) throws IOException {
        ASN1InputStream in = new ASN1InputStream(data);
        try
        {
            return in.readObject();
        }
        finally
        {
            in.close();
        }
    }

    /**
     * Возвращает информацию об использовании памяти
     */
    public static JSONObject getMemoryInfo()
    {
        Runtime runtime = Runtime.getRuntime();
        NumberFormat format = NumberFormat.getInstance();

        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long mb = 1024 * 1024;
        String suffix = "MB";

        JSONObject result = new JSONObject();
        result.put("free", format.format(freeMemory / mb) + suffix);
        result.put("allocated", format.format(allocatedMemory / mb) + suffix);
        result.put("max", format.format(maxMemory / mb) + suffix);
        result.put("totalFree", format.format((freeMemory + (maxMemory - allocatedMemory)) / mb) + suffix);

        return result;
    }

    /**
     * Крутой баннер прям как в кряках)))
     * @return
     */
    public static String awesomeBanner()
    {
        return "  _   _  ____    _    _   _           _      \n" +
                " | \\ | |/ ___|  / \\  | \\ | | ___   __| | ___ \n" +
                " |  \\| | |     / _ \\ |  \\| |/ _ \\ / _` |/ _ \\\n" +
                " | |\\  | |___ / ___ \\| |\\  | (_) | (_| |  __/\n" +
                " |_| \\_|\\____/_/   \\_\\_| \\_|\\___/ \\__,_|\\___|\n" +
                "                                             \n";
    }
}
