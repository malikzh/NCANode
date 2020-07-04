package kz.ncanode.api.version.v20.controllers;

import kz.ncanode.api.core.ApiStatus;
import kz.ncanode.api.core.annotations.ApiController;
import kz.ncanode.api.core.annotations.ApiMethod;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v20.models.InfoPKCS12AliasesModel;
import kz.ncanode.api.version.v20.models.InfoPKCS12Model;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

@ApiController("info")
public class InfoController extends kz.ncanode.api.core.ApiController {

    @ApiMethod(url = "pkcs12")
    public void pkcs12(InfoPKCS12Model model, JSONObject response) throws ApiErrorException {
        KeyStore p12 = model.p12.get();

        // Ищем алиас
        String alias = model.alias.get();

        if (alias == null || alias.isEmpty()) {
            Enumeration<String> als = null;
            try {
                als = p12.aliases();
            } catch (KeyStoreException e) {
                throw new ApiErrorException(e.getMessage());
            }

            while (als.hasMoreElements()) {
                alias = als.nextElement();
            }
        }

        try {
            if (!p12.containsAlias(alias)) {
                throw new ApiErrorException("Certificate with specified alias not found");
            }
        } catch (KeyStoreException e) {
            throw new ApiErrorException(e.getMessage());
        }


        // Читаем сертификат
        X509Certificate cert;

        try {
            cert = (X509Certificate)p12.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new ApiErrorException(e.getMessage());
        }

        // Chain information
        ArrayList<X509Certificate> chain = null;
        ArrayList<JSONObject> chainInf = null;
        try {
            chain = getApiServiceProvider().ca.chain(cert);

            chainInf = new ArrayList<>();

            if (chain != null) {
                for (X509Certificate chainCert : chain) {
                    JSONObject chi = getApiServiceProvider().pki.certInfo(chainCert, false, false, null);
                    chainInf.add(chi);
                }
            }
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }

        X509Certificate issuerCert = null;

        if (chain != null && chain.size() > 1) {
            issuerCert = chain.get(1);
        }

        try {
            JSONObject resp;
            resp = getApiServiceProvider().pki.certInfo(cert, (model.checkOcsp.get() && issuerCert != null) , (model.checkCrl.get() && issuerCert != null), issuerCert);
            resp.put("chain", chainInf);
            resp.put("alias", alias);
            response.put("certificate", resp);
            response.put("status", ApiStatus.STATUS_OK);
            response.put("message", "");
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }
    }

    @ApiMethod(url = "pkcs12aliases")
    public void pkcs12aliases(InfoPKCS12AliasesModel model, JSONObject response) throws ApiErrorException {
        KeyStore p12 = model.p12.get();

        JSONArray aliases = new JSONArray();

        Enumeration<String> als = null;
        try {
            als = p12.aliases();
        } catch (KeyStoreException e) {
            throw new ApiErrorException(e.getMessage());
        }

        while (als.hasMoreElements()) {
            aliases.add(als.nextElement());
        }

        response.put("status", ApiStatus.STATUS_OK);
        response.put("message", "");
        response.put("aliases", aliases);
    }
}
