package kz.ncanode.service;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.ncanode.constants.MessageConstants;
import kz.ncanode.dto.certificate.CertificateInfo;
import kz.ncanode.dto.certificate.CertificateRevocation;
import kz.ncanode.dto.request.Pkcs12InfoRequest;
import kz.ncanode.dto.response.VerificationResponse;
import kz.ncanode.exception.ServerException;
import kz.ncanode.wrapper.CertificateWrapper;
import kz.ncanode.wrapper.KalkanWrapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CertificateService {
    public final CrlService crlService;
    public final OcspService ocspService;
    public final CaService caService;
    public final KalkanWrapper kalkanWrapper;

    public void attachValidationData(final CertificateWrapper cert, boolean checkOcsp, boolean checkCrl) {
        cert.setIssuerCertificate(caService.getRootCertificateFor(cert).orElse(null));
        cert.setOcspStatus(checkOcsp ? ocspService.verify(cert, cert.getIssuerCertificate()) : null);
        cert.setCrlStatus(checkCrl ? crlService.verify(cert) : null);
    }

    public Date getCurrentDate() {
        return new Date();
    }

    public VerificationResponse verifyCerts(Pkcs12InfoRequest request) {
        var valid = true;
        val date = getCurrentDate();
        val withOcsp = request.getRevocationCheck().contains(CertificateRevocation.OCSP);
        val withCrl = request.getRevocationCheck().contains(CertificateRevocation.CRL);

        val keys = Optional.of(request.getKeys()).map(kalkanWrapper::read).orElseThrow();
        val certs = new ArrayList<CertificateInfo>();

        for (var key : keys) {
            val cert = key.getCertificate();

            attachValidationData(cert, withOcsp, withCrl);

            if (!cert.isValid(date, withOcsp, withCrl)) {
                valid = false;
            }

            certs.add(cert.toCertificateInfo(date, withOcsp, withCrl));
        }

        return VerificationResponse.builder()
            .valid(valid)
            .signers(certs)
            .build();
    }

    public VerificationResponse info(List<String> certsBase64, boolean checkOcsp, boolean checkCrl) {
        try {
            var valid = true;
            val currentDate = getCurrentDate();
            val certs = new ArrayList<CertificateInfo>();

            var message = "OK";
            var i = 0;

            for (String certBase64 : certsBase64) {
                var x509 = load(Base64.getDecoder().decode(certBase64.replaceAll("\\s", "")));

                if (x509 == null) {
                    message = String.format(MessageConstants.CERT_INVALID, i);
                    certs.add(null);
                    ++i;
                    valid = false;
                    continue;
                }

                val cert = new CertificateWrapper(x509);

                attachValidationData(cert, checkOcsp, checkCrl);

                if (!cert.isValid(currentDate, checkOcsp, checkCrl)) {
                    valid = false;
                }

                certs.add(cert.toCertificateInfo(currentDate, checkOcsp, checkCrl));
                ++i;
            }

            if (certsBase64.isEmpty()) {
                valid = false;
            }

            return VerificationResponse.builder()
                .valid(valid)
                .signers(certs)
                .message(message)
                .build();
        } catch (CertificateException|NoSuchProviderException|IOException e) {
            throw new ServerException(e.getMessage(), e);
        }
    }

    public static X509Certificate load(byte[] cert) throws CertificateException, NoSuchProviderException, IOException {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(cert)) {
            return (X509Certificate)java.security.cert.CertificateFactory.getInstance("X.509", KalkanProvider.PROVIDER_NAME).generateCertificate(stream);
        }
    }
}
