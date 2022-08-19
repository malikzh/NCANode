package kz.ncanode.util;

import kz.gov.pki.kalkan.asn1.pkcs.PKCSObjectIdentifiers;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedDataGenerator;
import kz.gov.pki.kalkan.tsp.TSPAlgorithms;
import org.apache.xml.security.encryption.XMLCipherParameters;
import org.apache.xml.security.utils.Constants;

/**
 * Вспомогательные методы для работы с KalkanCrypt
 */
public class KalkanUtil {
    /**
     * Метод возвращает алгоритм подписи по OID.
     *
     * @param oid OID
     * @return Массив с двумя элементами (Первый = Алгоритм подписи, второй = Алгоритм хэширования)
     */
    public static String[] getSignMethodByOID(String oid) {

        String[] ret = new String[2];


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
     * Возвращает алгоритм хэширования по алгоритму подписи.
     *
     * @param signOid sign OID
     * @return digest algorithm OID
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
     * Возвращает алгоритм подписи по OID.
     *
     * @param signOid ObjectID
     * @return Algorithm name
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
}
