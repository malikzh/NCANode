package kz.ncanode.wrapper;

import kz.gov.pki.kalkan.asn1.DERIA5String;
import kz.gov.pki.kalkan.asn1.x509.*;
import kz.gov.pki.kalkan.x509.extension.X509ExtensionUtil;
import kz.ncanode.dto.certificate.CertificateInfo;
import kz.ncanode.dto.certificate.CertificateKeyUsage;
import kz.ncanode.dto.certificate.CertificateKeyUser;
import kz.ncanode.dto.certificate.CertificateSubject;
import kz.ncanode.util.KalkanUtil;
import kz.ncanode.util.Util;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x509.Extension;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.IOException;
import java.net.URL;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CertificateWrapper {

    @Getter
    private final X509Certificate x509Certificate;

    private final String[] signAlg;

    public CertificateWrapper(X509Certificate certificate) {
        x509Certificate = certificate;
        signAlg = KalkanUtil.getSignMethodByOID(x509Certificate.getSigAlgOID());
    }

    /**
     * Возвращает ID алгоритма подписи
     *
     * @return String
     */
    public String getSignAlgorithmId() {
        return signAlg[0];
    }

    /**
     * Возвращает ID алгоритма хэширования
     *
     * @return String
     */
    public String getHashAlgorithmId() {
        return signAlg[1];
    }

    /**
     * Создает объект CertificateInfo
     * @return CertificateInfo
     */
    public CertificateInfo toCertificateInfo() {
        final X509Certificate cert = getX509Certificate();

        return CertificateInfo.builder()
            .valid(false)
            .revokedBy(Collections.emptySet())
            .notBefore(cert.getNotBefore())
            .notAfter(cert.getNotAfter())
            .keyUsage(CertificateKeyUsage.fromKeyUsageBits(cert.getKeyUsage()))
            .serialNumber(cert.getSerialNumber().toString(16))
            .signAlg(cert.getSigAlgName())
            .keyUser(getKeyUser())
            .publicKey(new String(Base64.getEncoder().encode(cert.getPublicKey().getEncoded())))
            .signature(new String(Base64.getEncoder().encode(cert.getSignature())))
            .subject(createCertificateSubjectFromDn(cert.getSubjectX500Principal().toString()).orElse(null))
            .issuer(createCertificateSubjectFromDn(cert.getIssuerX500Principal().toString()).orElse(null))
            .build();

    }

    /**
     * Получает список CRL из сертификата
     *
     * @return
     */
    public List<URL> getCrlList() {
        byte[] crlDistributionPoint = getX509Certificate().getExtensionValue(Extension.cRLDistributionPoints.getId());
        CRLDistPoint distPoint = null;
        try {
            distPoint = CRLDistPoint.getInstance(X509ExtensionUtil.fromExtensionValue(crlDistributionPoint));
        } catch (IOException e) {
            return Collections.emptyList();
        }

        List<String> crls = new ArrayList<>();

        for (DistributionPoint dp : distPoint.getDistributionPoints()) {
            DistributionPointName dpn = dp.getDistributionPoint();

            if (dpn != null) {
                if (dpn.getType() == DistributionPointName.FULL_NAME) {
                    GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
                    // Look for an URI
                    for (int j = 0; j < genNames.length; j++) {
                        if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
                            String url = DERIA5String.getInstance(genNames[j].getName()).getString();
                            crls.add(url);
                        }
                    }
                }
            }
        }

        return crls.stream().map(u -> Util.createNewUrl(u, log)).filter(Objects::nonNull).toList();
    }

    private Set<CertificateKeyUser> getKeyUser() {
        try {
            return getX509Certificate().getExtendedKeyUsage().stream()
                .map(CertificateKeyUser::fromOID)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        } catch (CertificateParsingException e) {
            log.error("Certificate key user extracting error", e);
            return Collections.emptySet();
        }
    }

    private static Optional<CertificateSubject> createCertificateSubjectFromDn(String dn) {
        try {
            final LdapName ldapName = new LdapName(dn);
            var subjectBuilder = CertificateSubject.builder();

            for (Rdn rdn : ldapName.getRdns()) {
                if (rdn.getType().equalsIgnoreCase("CN")) {
                    subjectBuilder.commonName((String)rdn.getValue());
                } else if (rdn.getType().equalsIgnoreCase("SURNAME")) {
                    subjectBuilder.surName((String)rdn.getValue());
                } else if (rdn.getType().equalsIgnoreCase("SERIALNUMBER")) {

                    String sn = ((String) rdn.getValue());

                    if (sn.startsWith("BIN")) {
                        subjectBuilder.bin(((String) rdn.getValue()).replaceAll("^BIN", ""));
                    } else {
                        subjectBuilder.iin(((String) rdn.getValue()).replaceAll("^IIN", ""));
                    }
                } else if (rdn.getType().equalsIgnoreCase("C")) {
                    subjectBuilder.country((String)rdn.getValue());
                } else if (rdn.getType().equalsIgnoreCase("L")) {
                    subjectBuilder.locality((String)rdn.getValue());
                } else if (rdn.getType().equalsIgnoreCase("S")) {
                    subjectBuilder.state((String)rdn.getValue());
                } else if (rdn.getType().equalsIgnoreCase("E")) {
                    subjectBuilder.email((String)rdn.getValue());
                } else if (rdn.getType().equalsIgnoreCase("O")) {
                    subjectBuilder.organization((String)rdn.getValue());
                } else if (rdn.getType().equalsIgnoreCase("OU")) {
                    subjectBuilder.bin(((String) rdn.getValue()).replaceAll("^BIN", ""));
                } else if (rdn.getType().equalsIgnoreCase("G")) {
                    subjectBuilder.lastName((String)rdn.getValue());
                }


            }

            subjectBuilder.dn(dn);

            return Optional.of(subjectBuilder.build());
        } catch (InvalidNameException e) {
            log.warn("Distinguished name parseing error", e);
            return Optional.empty();
        }
    }
}
