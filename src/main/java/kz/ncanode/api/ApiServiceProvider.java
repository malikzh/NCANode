package kz.ncanode.api;

import kz.ncanode.api.core.ApiStatus;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.version.v10.ApiVersion10;
import kz.ncanode.api.version.v20.ApiVersion20;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.info.InfoServiceProvider;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.kalkan.KalkanServiceProvider;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.RequestLogServiceProvider;
import kz.ncanode.pki.CAStoreServiceProvider;
import kz.ncanode.pki.CrlServiceProvider;
import kz.ncanode.pki.PkiServiceProvider;
import kz.ncanode.pki.TSPServiceProvider;
import org.json.simple.JSONObject;

import java.util.Hashtable;

/**
 * Данный класс получает запрос на обработку API,
 * И передает на обработку нужно версии (ApiVersion)
 */
public class ApiServiceProvider implements ServiceProvider {

    public ConfigServiceProvider config;
    public RequestLogServiceProvider req;
    public ErrorLogServiceProvider err;
    public PkiServiceProvider pki;
    public CrlServiceProvider crl;
    public CAStoreServiceProvider ca;
    public InfoServiceProvider info;
    public TSPServiceProvider tsp;
    public KalkanServiceProvider kalkan;

    public Hashtable<String, ApiVersion> supportedVersions = null;

    public ApiServiceProvider(ConfigServiceProvider config, RequestLogServiceProvider req, ErrorLogServiceProvider err, PkiServiceProvider pki, CrlServiceProvider crl, CAStoreServiceProvider ca, InfoServiceProvider info, TSPServiceProvider tsp, KalkanServiceProvider kalkan)
    {
        this.config = config;
        this.req    = req;
        this.err    = err;
        this.pki    = pki;
        this.crl    = crl;
        this.ca     = ca;
        this.info   = info;
        this.tsp    = tsp;
        this.kalkan = kalkan;

        // Роутинг версий
        supportedVersions = new Hashtable<>();

        supportedVersions.put("1.0", new ApiVersion10(this));
        supportedVersions.put("2.0", new ApiVersion20(this));
    }

    /**
     * Обрабатывает API запрос
     * @param request сам запрос
     * @return ответ от API
     */
    public JSONObject process(JSONObject request)
    {

        String apiVer;

        try {
            apiVer = (String)request.get("version");
        } catch (ClassCastException e) {
            JSONObject resp = new JSONObject();
            resp.put("status", ApiStatus.STATUS_INVALID_PARAMETER);
            resp.put("message", "Invalid parameter \"version\"");
            return resp;
        }

        // Требуем указывать версию
        if (apiVer == null || apiVer.isEmpty()) {
            JSONObject resp = new JSONObject();
            resp.put("status", ApiStatus.STATUS_VERSION_NOT_SPECIFIED);
            resp.put("message", "API version not specified");
            return resp;
        }

        // Получаем нужную версию API
        if (!supportedVersions.containsKey(apiVer)) {
            JSONObject resp = new JSONObject();
            resp.put("status", ApiStatus.STATUS_VERSION_NOT_SUPPORTED);
            resp.put("message", "API version not supported");
            return resp;
        }

        ApiVersion ver = supportedVersions.get(apiVer);

        ver.setApiManager(this);
        JSONObject resp = ver.process(request);

        req.write("Request: " + request.toJSONString() + "");

        return resp;
    }
}
