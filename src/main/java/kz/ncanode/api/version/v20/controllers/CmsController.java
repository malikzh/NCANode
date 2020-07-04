package kz.ncanode.api.version.v20.controllers;

import kz.gov.pki.kalkan.asn1.cms.AttributeTable;
import kz.gov.pki.kalkan.asn1.knca.KNCAObjectIdentifiers;
import kz.gov.pki.kalkan.jce.provider.cms.*;
import kz.gov.pki.kalkan.tsp.TSPException;
import kz.ncanode.Helper;
import kz.ncanode.api.core.ApiStatus;
import kz.ncanode.api.core.annotations.ApiController;
import kz.ncanode.api.core.annotations.ApiMethod;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v20.models.CmsSignModel;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.*;

@ApiController("cms")
public class CmsController extends kz.ncanode.api.core.ApiController {
    @ApiMethod(url = "sign")
    public void sign(CmsSignModel model, JSONObject response) throws KeyStoreException, ApiErrorException, UnrecoverableKeyException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, InvalidAlgorithmParameterException, CertStoreException, CMSException, IOException, TSPException {
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        CMSProcessableByteArray cmsData = new CMSProcessableByteArray(model.data.get());

        List<X509Certificate> certs = new ArrayList<>();

        for (int i=0; i<model.p12array.size(); ++i) {
            KeyStore p12      = model.p12array.getKey(i);
            String password   = model.p12array.getPassword(i);
            String alias      = model.p12array.getAlias(i);

            if (alias == null || alias.isEmpty()) {
                Enumeration<String> als = p12.aliases();

                while (als.hasMoreElements()) {
                    alias = als.nextElement();
                }
            }

            if (!p12.containsAlias(alias)) {
                throw new ApiErrorException("Certificate with specified alias not found");
            }

            // Получаем закрытый ключ
            PrivateKey privateKey = (PrivateKey) p12.getKey(alias, password.toCharArray());

            // Получаем сертификат
            X509Certificate cert = (X509Certificate) p12.getCertificate(alias);
            certs.add(cert);

            Signature sig = null;
            sig = Signature.getInstance(cert.getSigAlgName(), getApiServiceProvider().kalkan.get());
            sig.initSign(privateKey);
            sig.update(model.data.get());

            CertStore chainStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(cert)),getApiServiceProvider().kalkan.get().getName());
            gen.addSigner(privateKey, cert, Helper.getDigestAlgorithmOidBYSignAlgorithmOid(cert.getSigAlgOID()));
            gen.addCertificatesAndCRLs(chainStore);
        }

        CMSSignedData signed = gen.generate(cmsData, true, getApiServiceProvider().kalkan.get().getName());


        if (model.withTsp.get()) {
            String useTsaPolicy = model.useTsaPolicy.get().equals("TSA_GOSTGT_POLICY") ?
                    KNCAObjectIdentifiers.tsa_gostgt_policy.getId() :
                    KNCAObjectIdentifiers.tsa_gost_policy.getId();

            SignerInformationStore signerStore = signed.getSignerInfos();

            List<SignerInformation> newSigners = new ArrayList<SignerInformation>();

            int i=0;

            for (SignerInformation signer : (Collection<SignerInformation>)signerStore.getSigners())
            {
                X509Certificate cert = certs.get(i++);
                newSigners.add(getApiServiceProvider().tsp.addTspToSigner(signer, cert, useTsaPolicy));
            }

            signed = CMSSignedData.replaceSigners(signed, new SignerInformationStore(newSigners));
        }


        response.put("cms", new String(Base64.getEncoder().encode(signed.getEncoded())));
    }

    @ApiMethod(url = "verify")
    public void verify(JSONObject resp) throws ApiErrorException {

    }

    @ApiMethod(url = "extract")
    public void extract(JSONObject resp) throws ApiErrorException {

    }
}
