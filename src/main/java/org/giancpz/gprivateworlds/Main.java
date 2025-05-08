package org.giancpz.gprivateworlds;

import org.bukkit.plugin.java.JavaPlugin;
import org.giancpz.gprivateworlds.commands.WorldsCommands;
import org.giancpz.gprivateworlds.commands.tab;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class Main extends JavaPlugin
{
    private static Main main;
    public JoinManager joinManager;
    public PluginConfig pluginOptions;
    public Queue queue;
    public sql db;
    public Node node;
    public AdminWorld adminWorld;
    public String templatechecksum;

    @Override
    public void onEnable()
    {
        main = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        pluginOptions = new PluginConfig();
        PluginConfig.LoadConfig();
        joinManager = new JoinManager();
        queue = new Queue();
        queue.Thread();
        node = new Node();
        db = new sql();
        node.InitNode();

        getServer().getPluginManager().registerEvents(new mListener(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Objects.requireNonNull(getCommand("privateworld")).setExecutor(new WorldsCommands());
        Objects.requireNonNull(getServer().getPluginCommand("privateworld")).setTabCompleter(new tab());

        db.initializeDatabase();

        //getServer().getServicesManager().register(Core.class, this, this, ServicePriority.Normal);

        String td = "worldtemplate";
        File template = new File(td);

        if(template.exists())
        {
            templatechecksum =  Checksum.Get(td + "/region");

            getLogger().info("Loading world template...");
            try {
                Zip.zip(td,"worldtemplate.zip");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // *** Only local mode
        if(PluginConfig.Options().worldstoragemethod == PluginConfig.Worldsstoragemethod.FTP)
        {
            String[] dirs = {PluginConfig.Options().PlayerDataDirectory, pluginOptions.WorldDataDirectory, "GPrivateWorlds/local/temp", "GPrivateWorlds/local/worlds"};

            for (String dir : dirs) {
                File d = new File(dir);
                if (!d.exists()) {
                    if (d.mkdirs()) {
                        getLogger().info("new dir: " + d.getAbsolutePath());
                    }
                }
            }
        }
        getLogger().info("Plugin enabled!");

        int pluginId = 25793; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);

        UpdateChecker.Check();
    }

    @Override
    public void onDisable() {
        Internal.saveall();
        db.CloseConnection();
    }

    public static Main Singleton() {
        return Main.main;
    }
}
