package kz.ncanode.info;

import kz.ncanode.ioc.ServiceProvider;

public class InfoServiceProvider implements ServiceProvider {
    private final static String VERSION = "0.1";

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
