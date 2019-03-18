package kz.ncanode;

import kz.gov.pki.kalkan.tsp.TSPAlgorithms;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
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
}
