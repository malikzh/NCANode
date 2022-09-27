package kz.ncanode.service;

import kz.gov.pki.kalkan.asn1.pkcs.Attribute;
import kz.gov.pki.kalkan.asn1.pkcs.PKCSObjectIdentifiers;
import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.jce.provider.cms.*;
import kz.gov.pki.kalkan.tsp.TimeStampTokenInfo;
import kz.gov.pki.kalkan.util.encoders.Hex;
import kz.ncanode.dto.cms.CmsSignerInfo;
import kz.ncanode.dto.request.CmsCreateRequest;
import kz.ncanode.dto.response.CmsResponse;
import kz.ncanode.dto.response.CmsVerificationResponse;
import kz.ncanode.dto.tsp.TsaPolicy;
import kz.ncanode.dto.tsp.TspInfo;
import kz.ncanode.exception.ClientException;
import kz.ncanode.exception.ServerException;
import kz.ncanode.util.KalkanUtil;
import kz.ncanode.util.Util;
import kz.ncanode.wrapper.CertificateWrapper;
import kz.ncanode.wrapper.KalkanWrapper;
import kz.ncanode.wrapper.KeyStoreWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class CmsService {

    private final KalkanWrapper kalkanWrapper;
    private final TspService tspService;
    private final  CertificateService certificateService;

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

    public CmsVerificationResponse verify(String signedCms, boolean checkOcsp, boolean checkCrl) {
        try {
            CMSSignedData cms = new CMSSignedData(Base64.getDecoder().decode(signedCms.getBytes(StandardCharsets.UTF_8)));
            CertStore certStore = cms.getCertificatesAndCRLs("Collection", KalkanProvider.PROVIDER_NAME);
            val signerIt = cms.getSignerInfos().getSigners().iterator();

            final List<CmsSignerInfo> signers = new ArrayList<>();

            boolean valid = true;

            val currentDate = certificateService.getCurrentDate();

            while (signerIt.hasNext()) {
                var signerInfoBuilder = CmsSignerInfo.builder();

                var signer = (SignerInformation) signerIt.next();
                X509CertSelector signerConstraints = signer.getSID();
                var certCollection = certStore.getCertificates(signerConstraints);

                var certIt = certCollection.iterator();

                while (certIt.hasNext()) {
                    CertificateWrapper cert = new CertificateWrapper((X509Certificate) certIt.next());

                    if (!signer.verify(cert.getPublicKey(), KalkanProvider.PROVIDER_NAME) || !cert.isValid(currentDate, checkOcsp, checkCrl)) {
                        valid = false;
                    }

                    signerInfoBuilder.certificate(cert.toCertificateInfo(currentDate, checkOcsp, checkCrl));
                }

                // TSP Checking
                if (signer.getUnsignedAttributes() != null) {
                    var attrs = signer.getUnsignedAttributes().toHashtable();

                    if (attrs.containsKey(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken)) {
                        Attribute attr = (Attribute) attrs.get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);


                        if (attr.getAttrValues().size() != 1) {
                            throw new Exception("Too many TSP tokens");
                        }

                        CMSSignedData tspCms = new CMSSignedData(attr.getAttrValues().getObjectAt(0).getDERObject().getEncoded());
                        TimeStampTokenInfo tspi = tspService.info(tspCms).orElseThrow();

                        TspInfo tspInfo = TspInfo.builder()
                            .serialNumber(new String(Hex.encode(tspi.getSerialNumber().toByteArray())))
                            .genTime(tspi.getGenTime())
                            .policy(tspi.getPolicy())
                            .tsa(tspi.getTsa().toString())
                            .tspHashAlgorithm(KalkanUtil.getHashingAlgorithmByOID(tspi.getMessageImprintAlgOID()))
                            .hash(new String(Hex.encode(tspi.getMessageImprintDigest())))
                            .build();

                        signerInfoBuilder.tsp(tspInfo);
                    }
                }

                signers.add(signerInfoBuilder.build());
            }

            return CmsVerificationResponse.builder()
                .valid(valid)
                .signers(signers)
                .build();
        } catch (Exception e) {
            throw new ClientException(e.getMessage(), e);
        }
    }
}
