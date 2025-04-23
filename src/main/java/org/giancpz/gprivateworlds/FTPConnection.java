package org.giancpz.gprivateworlds;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.util.UUID;

public class FTPConnection
{
    FTPClient ftpClient;

    public FTPConnection()
    {
        if(PluginConfig.Options().worldstoragemethod == PluginConfig.Worldsstoragemethod.FTP) {
            ftpClient = new FTPClient();
            try {
                ftpClient.connect(PluginConfig.Options().ftpconfig.address, PluginConfig.Options().ftpconfig.port);
                if (ftpClient.login(PluginConfig.Options().ftpconfig.username, PluginConfig.Options().ftpconfig.password)) {
                    Main.Singleton().getLogger().info("FTP authentication successful!");
                } else {
                    Main.Singleton().getLogger().severe("FTP authentication failed, user or password is incorrect! Please change user or password and use /pw dbreload");
                    ftpClient.disconnect();
                    return;
                }
                if (PluginConfig.Options().ftpconfig.passivemode) {
                    ftpClient.enterLocalPassiveMode();
                }
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                if (!ftpClient.isConnected()) {
                    Main.Singleton().getLogger().severe("Error connecting to FTP server");
                    ftpClient.disconnect();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void Download(UUID WorldUUID) {

        File directory = new File("GPrivateWorlds/multinode/temp/");
        if (!directory.exists() && !directory.mkdirs()) {
            Main.Singleton().getLogger().severe("Failed to create directory: " + directory.getAbsolutePath());
        }

        String path = "GPrivateWorlds/multinode/temp/" + WorldUUID + ".zip";
        File file = new File(path);

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (OutputStream outputStream = new FileOutputStream(file)) {
            boolean dowload = ftpClient.retrieveFile("/worlds/" + WorldUUID + ".zip", outputStream);
            if (!dowload) {
                Main.Singleton().getLogger().severe("Error to download file");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error de E/S: " + e.getMessage());
            e.printStackTrace();
        }

        //long t = System.currentTimeMillis();
        //long d = t - d;
        //System.out.println("d + " ms");
    }
    public void Load(UUID WorldUUID)
    {
        String archivoLocal = "GPrivateWorlds/multinode/temp/" + WorldUUID + ".zip";
        String archivoRemoto = "/worlds/" + WorldUUID + ".zip";

        try (FileInputStream fis = new FileInputStream(archivoLocal)) {
            boolean hecho = ftpClient.storeFile(archivoRemoto, fis);
            if (hecho) {
                System.out.println("El archivo se subió correctamente.");
            } else {
                System.out.println("Falló la subida del archivo.");
            }
        } catch (IOException e)
        {
            System.out.println(e);
            System.out.println(e.getMessage());
        }
    }
}
