package kz.ncanode.config;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

public class Config {
    protected Ini ini = null;

    public Config(String fileName) throws IOException {
        ini = new Ini(new File(fileName));
    }

    public Config() {
    }

    public String get(String sectionName, String optionName, String defaultValue) {
        if (ini == null) {
            return defaultValue;
        }

        String value = ini.get(sectionName, optionName);

        if (value == null) {
            value = defaultValue;
        }

        return value;
    }
}
