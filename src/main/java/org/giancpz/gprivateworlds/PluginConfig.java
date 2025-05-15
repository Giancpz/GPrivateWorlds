package org.giancpz.gprivateworlds;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static org.giancpz.gprivateworlds.WorldOptions.*;

public class PluginConfig
{
    public YamlDocument config;

    int WorldSizeX;
    int WorldSizeY;
    int WorldSizeNY;
    int WorldSizeZ;
    int VoidTeleport;
    int ViewDistance;
    int SimulationDistance;
    int WorldBorder;
    Long queuePeriod = 10L;
    int inactivity = 900;
    boolean SpawnMobs;
    boolean MultiMembers;
    boolean AcceptVisits;
    boolean debug = false;
    SpawnLocation spawnLocation;
    Mysql mysql = new Mysql();
    Ftpconfig ftpconfig = new Ftpconfig();
    Storagemethod playerstoragemethod;
    Worldsstoragemethod worldstoragemethod;
    Nodemode nodemode = Nodemode.LOCAL; // TEMPORAL
    List<String> defaultWorldOptions = new ArrayList<>();
    List<String> gamerules = new ArrayList<>();

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
        MYSQL,
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


    public void LoadConfig()
    {
        try {
            config = YamlDocument.create(new File(Main.Singleton().getDataFolder(), "config.yml"), Main.Singleton().getResource("config.yml"),
                    GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("version")).build());
            config.update();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        queuePeriod = config.getLong("queue-period");
        inactivity = config.getInt("inactivity-time");

        playerstoragemethod = Enum.valueOf(Storagemethod.class, Objects.requireNonNull(config.getString("storage-method")).toUpperCase());

        // Mysql
        mysql.address = config.getString("storage-method-config.address");
        mysql.port = config.getInt("storage-method-config.port");
        mysql.username = config.getString("storage-method-config.username");
        mysql.password = config.getString("storage-method-config.password");
        mysql.database = config.getString("storage-method-config.database");
        mysql.maxpoolsize = config.getInt("storage-method-config.maximum-pool-size");
        mysql.ssl = config.getBoolean("storage-method-config.ssl");

        // options.nodemode = Enum.valueOf(Nodemode.class, Objects.requireNonNull(config.getString("node-mode")).toUpperCase());

        worldstoragemethod = Enum.valueOf(Worldsstoragemethod.class, Objects.requireNonNull(config.getString("world-storage-method")).toUpperCase());
        //ftp
        ftpconfig.address = config.getString("ftp-config.address");
        ftpconfig.port = config.getInt("ftp-config.port");
        ftpconfig.username = config.getString("ftp-config.username");
        ftpconfig.password = config.getString("ftp-config.password");
        ftpconfig.remotedirectory = config.getString("ftp-config.remote-directory");
        ftpconfig.passivemode = config.getBoolean("ftp-config.passive-mode");
        ftpconfig.tsl = config.getBoolean("ftp-config.tsl");

        WorldSizeX = config.getInt("world-limit.x");
        WorldSizeY = config.getInt("world-limit.y");
        WorldSizeNY = config.getInt("world-limit.ny");
        WorldSizeZ = config.getInt("world-limit.z");

        SimulationDistance = config.getInt("simulation-distance");
        WorldBorder = config.getInt("world-border");
        ViewDistance = config.getInt("view-distance");

        spawnLocation = new SpawnLocation();

        spawnLocation.set(config.getInt("default-spawn-location.x"),
                            config.getInt("default-spawn-location.y"),
                            config.getInt("default-spawn-location.z"));

        VoidTeleport = config.getInt("void-teleport");

        SpawnMobs = config.getBoolean("spawn-mobs");
        MultiMembers = config.getBoolean("multi-members");
        AcceptVisits = config.getBoolean("accept-visit");
        gamerules = config.getStringList("game-rules");
        defaultWorldOptions = config.getStringList("default-world-options");

        debug = config.getBoolean("debug");
    }

    public static PluginConfig Options()
    {
        return Main.Singleton().pluginOptions;
    }
}
