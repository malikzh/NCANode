package kz.ncanode.log;

import kz.ncanode.Helper;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.ioc.ServiceProvider;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Класс логирования, который предназначен, для логирования запросов по API (request.log)
 */
public class RequestLogServiceProvider extends Log implements ServiceProvider {
    OutLogServiceProvider out;
    ConfigServiceProvider config;

    public RequestLogServiceProvider(ConfigServiceProvider config, OutLogServiceProvider out) {

        this.out = out;
        this.config = config;

        String requestLogFile = "";
        try {
            requestLogFile = this.config.get("log", "request_log");

            if (requestLogFile == null || requestLogFile.isEmpty()) {
                return;
            }

            requestLogFile = Helper.absolutePath(requestLogFile);

            os = new FileOutputStream(requestLogFile, true);
            ps = new LogPrintStream(os);
        } catch (FileNotFoundException e) {
            this.out.write("WARNING! Cannot create request log file at: " + requestLogFile);
        }
    }
}
