package kz.ncanode.info;

import kz.ncanode.ioc.ServiceProvider;

/**
 * Класс хранит информацию о запущенной ноде
 */
public class InfoServiceProvider implements ServiceProvider {
    public final static String VERSION = "2.3.0";
    public final static String PROJECT_PAGE = "https://ncanode.kz";
    public final static String GITHUB_PAGE = "https://github.com/malikzh/NCANode";

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
