package kz.ncanode.wrapper;

import kz.ncanode.dto.certificate.CertificateInfo;
import kz.ncanode.dto.certificate.CertificateKeyUsage;
import kz.ncanode.dto.certificate.CertificateKeyUser;
import kz.ncanode.dto.certificate.CertificateSubject;
import kz.ncanode.util.KalkanUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
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
