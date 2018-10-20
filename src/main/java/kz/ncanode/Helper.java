package kz.ncanode;

import java.io.File;
import java.nio.file.Paths;

public class Helper {
    public static String absolutePath(String path) {
        return Paths.get(path).toAbsolutePath().toString();
    }

    public static String fileExt(File file) {
        String ext = "";

        try {
            String name = file.getName();
            ext = name.substring(name.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            //
        }

        return ext;
    }
}
