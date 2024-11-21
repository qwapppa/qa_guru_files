package utils;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FilesUtils {

    private final String zipName = "files.zip";

    public InputStream getFileFromZip(String fileNameToExtract) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(
                getClass().getClassLoader().getResourceAsStream(zipName));
        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.getName().contains(fileNameToExtract)) {
                return zipInputStream;
            }
        }
        return InputStream.nullInputStream();
    }

    public String getZipName() {
        return zipName;
    }
}