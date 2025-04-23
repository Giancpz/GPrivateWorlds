package org.giancpz.gprivateworlds;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.Objects;
import static org.giancpz.gprivateworlds.WorldOptions.*;

public class PluginConfig
{
    int ConfigVersion = 1;
    int WorldSizeX;
    int WorldSizeY;
    int WorldSizeNY;
    int WorldSizeZ;
    int ViewDistance;
    int SimulationDistance;
    int WorldBorder;
    boolean SpawnMobs;
    boolean MultiMembers;
    boolean AcceptVisits;
    SpawnLocation spawnLocation;
    Mysql mysql = new Mysql();
    Ftpconfig ftpconfig = new Ftpconfig();
    Storagemethod playerstoragemethod;
    Worldsstoragemethod worldstoragemethod;
    Nodemode nodemode;

    String PlayerDataDirectory = "GPrivateWorlds/local/playerdata/";
    String WorldDataDirectory = "GPrivateWorlds/local/worlddata/";
    String WorldTemplateDirectoryZIP = "worldtemplate.zip";

    protected enum Nodemode
    {
        LOCAL,
        MAIN,
        CLIENT,
        ROUTER,
    }

    protected enum Storagemethod
    {
        H2,
        MARIADB
    }

    protected enum Worldsstoragemethod
    {
        LOCAL,
        FTP
    }

    public static class Mysql
    {
        String address;
        int port;
        String username;
        String password;
        String database;
        int maxpoolsize;
        boolean ssl;

        public static Mysql getConfig()
        {
            return Main.Singleton().pluginOptions.mysql;
        }
    }

    public static class Ftpconfig
    {
        String address;
        int port;
        String username;
        String password;
        String remotedirectory;
        boolean passivemode;
        boolean tsl;
    }

    public static void LoadConfig()
    {
        PluginConfig options = Main.Singleton().pluginOptions;
        FileConfiguration config = Main.Singleton().getConfig();

        options.ConfigVersion = config.getInt("ConfigVersion");

        options.playerstoragemethod = Enum.valueOf(Storagemethod.class, Objects.requireNonNull(config.getString("storage-method")).toUpperCase());

        // Mysql
        options.mysql.address = config.getString("storage-method-config.address");
        options.mysql.port = config.getInt("storage-method-config.port");
        options.mysql.username = config.getString("storage-method-config.username");
        options.mysql.password = config.getString("storage-method-config.password");
        options.mysql.database = config.getString("storage-method-config.database");
        options.mysql.maxpoolsize = config.getInt("storage-method-config.maximum-pool-size");
        options.mysql.ssl = config.getBoolean("storage-method-config.ssl");

        options.nodemode = Enum.valueOf(Nodemode.class, Objects.requireNonNull(config.getString("node-mode")).toUpperCase());

        options.worldstoragemethod = Enum.valueOf(Worldsstoragemethod.class, Objects.requireNonNull(config.getString("world-storage-method")).toUpperCase());

        //ftp
        options.ftpconfig.address = config.getString("ftp-config.address");
        options.ftpconfig.port = config.getInt("ftp-config.port");
        options.ftpconfig.username = config.getString("ftp-config.username");
        options.ftpconfig.password = config.getString("ftp-config.password");
        options.ftpconfig.remotedirectory = config.getString("ftp-config.remote-directory");
        options.ftpconfig.passivemode = config.getBoolean("ftp-config.passive-mode");
        options.ftpconfig.tsl = config.getBoolean("ftp-config.tsl");

        options.WorldSizeX = config.getInt("world-limit.x");
        options.WorldSizeY = config.getInt("world-limit.y");
        options.WorldSizeNY = config.getInt("world-limit.ny");
        options.WorldSizeZ = config.getInt("world-limit.z");

        options.SimulationDistance = config.getInt("simulation-distance");
        options.WorldBorder = config.getInt("world-border");
        options.ViewDistance = config.getInt("view-distance");

        options.spawnLocation = new SpawnLocation();
        options.spawnLocation.set(config.getInt("spawn-location.x"),
                            config.getInt("spawn-location.y"),
                            config.getInt("spawn-location.z"));

        options.SpawnMobs = config.getBoolean("spawn-mobs");
        options.MultiMembers = config.getBoolean("multi-members");
        options.AcceptVisits = config.getBoolean("accept-visit");
    }

    public static PluginConfig Options()
    {
        return Main.Singleton().pluginOptions;
    }
}
