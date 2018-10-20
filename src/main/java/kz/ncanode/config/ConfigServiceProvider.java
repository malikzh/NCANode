package kz.ncanode.config;

import kz.ncanode.Helper;
import kz.ncanode.cmd.CmdServiceProvider;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import org.ini4j.Ini;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ConfigServiceProvider extends Config implements ServiceProvider {

    public final static String CONFIG_FILENAME = "ncanode.ini";

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
        // [log]
        defaultIni.put("log", "error_log", "logs/error.log");
        defaultIni.put("log", "request_log", "logs/request.log");

        // [ca]
        defaultIni.put("ca", "root_dir", "ca/root");
        defaultIni.put("ca", "trusted_dir", "ca/trusted");

        // [pki]
        defaultIni.put("pki", "ocsp_url", "http://ocsp.pki.gov.kz");


    }
}
