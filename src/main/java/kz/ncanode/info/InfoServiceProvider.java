package kz.ncanode.info;

import kz.ncanode.ioc.ServiceProvider;

/**
 * Класс хранит информацию о запущенной ноде
 */
public class InfoServiceProvider implements ServiceProvider {
    public final static String VERSION = "0.1";
    public final static String PROJECT_PAGE = "http://ncanode.kz";

    public String getName() {
        return "NCANode";
    }

    public String getFullName() {
        return getName() + " v" + getVersion();
    }

    public String getVersion() {
        return VERSION;
    }
}
