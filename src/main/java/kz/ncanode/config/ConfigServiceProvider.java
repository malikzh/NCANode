package kz.ncanode.config;

import kz.ncanode.Helper;
import kz.ncanode.cmd.CmdServiceProvider;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import org.ini4j.Ini;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Класс для загрузки и хранения конфигурации
 */
public class ConfigServiceProvider extends Config implements ServiceProvider {

    public final static String CONFIG_FILENAME = "NCANode.ini";

    Ini defaultIni = null;

    // Dependencies
    private CmdServiceProvider cmd = null;
    private OutLogServiceProvider outLog = null;

    public ConfigServiceProvider(CmdServiceProvider cmd, OutLogServiceProvider outLog) {
        this.cmd    = cmd;
        this.outLog = outLog;

        String configFile = this.cmd.get("config");

        if (configFile == null) {
            configFile = CONFIG_FILENAME;
        }

        configFile = Helper.absolutePath(configFile);

        outLog.write("Trying to load config from: " + configFile);

        try {
            this.defaultIni = new Ini();
            setDefaultConfig();

            ini = new Ini(new File(configFile));
        } catch(FileNotFoundException e) {
            outLog.write("WARNING! Cannot load config from file. Using built-in config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String sectionName, String optionName) {
        return super.get(sectionName, optionName, defaultIni.get(sectionName, optionName));
    }

    /**
     * Встроенная конфигурация
     */
    private void setDefaultConfig() {
        // [main]
        defaultIni.put("main", "mode", "http");

        // [log]
        defaultIni.put("log", "error_log", "logs/error.log");
        defaultIni.put("log", "request_log", "logs/request.log");

        // [ca]
        defaultIni.put("ca", "root_dir", "ca/root");
        defaultIni.put("ca", "trusted_dir", "ca/trusted");

        // [pki]
        defaultIni.put("pki", "ocsp_url", "http://ocsp.pki.gov.kz");
        defaultIni.put("pki", "tsp_url", "http://tsp.pki.gov.kz");
        defaultIni.put("pki", "crl_enabled", "true");
        defaultIni.put("pki", "crl_urls", "http://crl.pki.gov.kz/nca_rsa.crl http://crl.pki.gov.kz/nca_gost.crl http://crl.pki.gov.kz/nca_d_rsa.crl  http://crl.pki.gov.kz/nca_d_gost.crl http://crl.pki.gov.kz/rsa.crl http://crl.pki.gov.kz/gost.crl http://crl.pki.gov.kz/d_rsa.crl http://crl.pki.gov.kz/d_gost.crl");
        defaultIni.put("pki", "crl_cache_dir", "cache/crl");
        defaultIni.put("pki", "crl_cache_lifetime", "60");

        // [http]
        defaultIni.put("http", "ip", "127.0.0.1");
        defaultIni.put("http", "port", "14579");

        // [rabbitmq]
        defaultIni.put("rabbitmq", "host", "127.0.0.1");
        defaultIni.put("rabbitmq", "port", "5672");
        defaultIni.put("rabbitmq", "queue_name", "ncanode");

    }
}
