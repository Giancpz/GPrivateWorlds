package org.giancpz.gprivateworlds;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {
    public static void unZip(String archivoZip, String destino) throws IOException
    {
        File directorioDestino = new File(destino);
        if (!directorioDestino.exists()) {
            directorioDestino.mkdirs(); // Crear el directorio de destino si no existe
        }

        try (FileInputStream fis = new FileInputStream(archivoZip);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entrada;
            while ((entrada = zis.getNextEntry()) != null) {
                File nuevoArchivo = new File(destino, entrada.getName());

                if (entrada.isDirectory()) {
                    // Crear subdirectorios si es necesario
                    nuevoArchivo.mkdirs();
                } else {
                    // Asegurarse de que el directorio padre existe
                    nuevoArchivo.getParentFile().mkdirs();

                    // Escribir el contenido del archivo
                    try (FileOutputStream fos = new FileOutputStream(nuevoArchivo)) {
                        byte[] buffer = new byte[2048];
                        int longitud;
                        while ((longitud = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, longitud);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    public static void zip(String sourceDir, String zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            File dir = new File(sourceDir);
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException("El argumento sourceDir debe ser un directorio.");
            }

            // Llamada inicial, usando la carpeta raíz pero dejando el path vacío
            for (File file : dir.listFiles()) {
                addFileToZip(file, "", zos);
            }
        }
    }

    private static void addFileToZip(File file, String parentPath, ZipOutputStream zos) throws IOException {
        String entryName = parentPath + file.getName();

        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                addFileToZip(childFile, entryName + "/", zos);
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry entry = new ZipEntry(entryName);
                zos.putNextEntry(entry);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();
            }
        }
    }
}
