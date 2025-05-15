package org.giancpz.gprivateworlds;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.IOException;

public class TemplateEditor
{
    static boolean isLoaded = false;
    static World world;

    public static void Start()
    {
        if(!isLoaded)
        {
            WorldCreator creator = new WorldCreator("worldtemplate");
            world = creator.createWorld();
            isLoaded = true;
        }
    }

    public static void Edit(Player player)
    {
        if(player.isOp())
        {
            Start();
            player.teleport(world.getSpawnLocation());
            player.setGameMode(GameMode.CREATIVE);
        }
    }

    public static void SetSpawn(Player player)
    {
        if(player.hasPermission("gprivateworlds.template.edit"))
        {
            if(player.getWorld().equals(world))
            {
                Location loc = player.getLocation();
                int x = loc.getBlockX();
                int y = loc.getBlockY();
                int z = loc.getBlockZ();
                PluginConfig.Options().config.set("default-spawn-location.x", x);
                PluginConfig.Options().config.set("default-spawn-location.y", y);
                PluginConfig.Options().config.set("default-spawn-location.z", z);

                PluginConfig.Options().spawnLocation.set(x, y, z);
                try {
                    PluginConfig.Options().config.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void Save()
    {
        if(!isLoaded) return;

        for (Player p : world.getPlayers())
        {
            World w =  Bukkit.getWorld("world");

            if (w != null)
            {
                p.teleport(w.getSpawnLocation());
            }
            else p.kickPlayer("Template save");
        }

        Bukkit.unloadWorld(world, true);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                Main.Singleton().queue.PauseQueue();
                Print.warning("Queue was paused because template is edit");
                Template.LoadTemplate();
                Print.info("Template save!");
                Main.Singleton().queue.ResumeQueue();
            }
        }.runTaskAsynchronously(Main.Singleton());
        isLoaded = false;
    }
}