package kz.ncanode.service;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.jce.provider.cms.*;
import kz.ncanode.dto.request.CmsCreateRequest;
import kz.ncanode.dto.response.CmsResponse;
import kz.ncanode.dto.tsp.TsaPolicy;
import kz.ncanode.exception.ServerException;
import kz.ncanode.util.Util;
import kz.ncanode.wrapper.CertificateWrapper;
import kz.ncanode.wrapper.KalkanWrapper;
import kz.ncanode.wrapper.KeyStoreWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.security.Signature;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CmsService {

    private final KalkanWrapper kalkanWrapper;
    private final TspService tspService;

    public CmsResponse create(CmsCreateRequest cmsCreateRequest) {
        try {
            CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            val data = Base64.getDecoder().decode(cmsCreateRequest.getData());

            CMSProcessable cmsData = new CMSProcessableByteArray(data);
            List<X509Certificate> certificates = new ArrayList<>();

            for (KeyStoreWrapper ks : kalkanWrapper.read(cmsCreateRequest.getSigners())) {
                CertificateWrapper cert = ks.getCertificate();
                val privateKey = ks.getPrivateKey();

                Signature sig = Signature.getInstance(cert.getX509Certificate().getSigAlgName(), kalkanWrapper.getKalkanProvider());
                sig.initSign(privateKey);
                sig.update(data);

                generator.addSigner(privateKey, cert.getX509Certificate(), Util.getDigestAlgorithmOidBYSignAlgorithmOid(cert.getX509Certificate().getSigAlgOID()));
                certificates.add(cert.getX509Certificate());
            }

            CertStore chainStore = CertStore.getInstance(
                "Collection",
                new CollectionCertStoreParameters(
                    // если происходит повторная подпись, сертификаты могут дублироваться.
                    // добавим в chainStore только уникальные сертификаты.
                    certificates
                ),
                KalkanProvider.PROVIDER_NAME
            );

            generator.addCertificatesAndCRLs(chainStore);
            CMSSignedData signed = generator.generate(cmsData, !cmsCreateRequest.isDetached(), KalkanProvider.PROVIDER_NAME);

            // TSP
            if (cmsCreateRequest.isWithTsp()) {
                String useTsaPolicy = Optional.ofNullable(cmsCreateRequest.getTsaPolicy()).map(TsaPolicy::getPolicyId)
                    .orElse(TsaPolicy.TSA_GOST_POLICY.getPolicyId());

                SignerInformationStore signerStore = signed.getSignerInfos();
                List<SignerInformation> signers = new ArrayList<>();

                int i = 0;

                for (Object signer : signerStore.getSigners()) {
                    X509Certificate cert = certificates.get(i++);
                    signers.add(tspService.addTspToSigner((SignerInformation) signer, cert, useTsaPolicy));
                }

                signed = CMSSignedData.replaceSigners(signed, new SignerInformationStore(signers));
            }

            return CmsResponse.builder()
                .cms(Base64.getEncoder().encodeToString(signed.getEncoded()))
                .build();
        } catch (Exception e) {
            throw new ServerException(e.getMessage(), e);
        }
    }
}
