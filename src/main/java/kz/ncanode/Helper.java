package kz.ncanode;

import java.nio.file.Paths;

public class Helper {
    public static String absolutePath(String path) {
        return Paths.get(path).toAbsolutePath().toString();
    }
}
