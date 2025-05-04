package org.giancpz.gprivateworlds;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;

public class Checksum
{
    public static String calculateCombinedChecksum(String directoryPath, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        File folder = new File(directoryPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    InputStream fis = Files.newInputStream(file.toPath());
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = fis.read(buffer)) != -1) {
                        digest.update(buffer, 0, bytesRead);
                    }
                    fis.close();
                }
            }
        } else {
            throw new Exception("No se encontraron archivos en la carpeta.");
        }

        byte[] hashBytes = digest.digest();
        StringBuilder checksum = new StringBuilder();
        for (byte b : hashBytes) {
            checksum.append(String.format("%02x", b));
        }
        return checksum.toString();
    }

    public static String Get(String filepath){
        try {
            Print.debug("Checking file: " + filepath);
            String combinedChecksum = calculateCombinedChecksum(filepath, "MD5");
            Print.debug("Checksum: " + combinedChecksum);
            return combinedChecksum;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }
}