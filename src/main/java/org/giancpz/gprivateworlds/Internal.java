package org.giancpz.gprivateworlds;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.FileUtils;
import org.giancpz.gprivateworlds.Queue.TaskInfo;
import org.giancpz.gprivateworlds.WorldOptions.*;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class Internal
{
    public static void AddManagerTask(Player player, String ExecutePlayerName, String toPlayer, TaskInfo.TaskType taskType, String otherparam)
    {
        TaskInfo task = new TaskInfo(taskType);
        task.SecondPlayer = toPlayer;
        task.ExecutePlayer = player;
        task.ExecutePlayerName = ExecutePlayerName;
        task.otherparam = otherparam;

        try {
            Main.Singleton().queue.queue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static World internal_LoadPlayerWorld(WorldInfo worldInfo) {

        String worldpath = null;
        if(PluginConfig.Options().nodemode == PluginConfig.Nodemode.LOCAL)
        {
            try {
                Zip.unZip("GPrivateWorlds/local/worlds/" + worldInfo.uuid + ".zip", "GPrivateWorlds/local/temp/" + worldInfo.uuid);
                worldpath = "GPrivateWorlds/local/temp/" + worldInfo.uuid;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
            Main.Singleton().queue.ftp.Download(worldInfo.uuid);
            try {
                Zip.unZip("GPrivateWorlds/multinode/temp/" + worldInfo.uuid + ".zip", "GPrivateWorlds/multinode/temp/" + worldInfo.uuid);
                worldpath = "GPrivateWorlds/multinode/temp/" + worldInfo.uuid;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        assert worldpath != null;
        String cheksum = Checksum.Get(worldpath + "/region");

        if(worldInfo.checkSum.equals(cheksum)) {
            Print.debug("Correct checksum!");

            WorldCreator creator = new WorldCreator(worldpath);
            World world = creator.createWorld();

            worldInfo.world = world;

            if(world != null)
            {
                world.setKeepSpawnInMemory(false);
                world.setSpawnLocation(worldInfo.spawnLocation.x, worldInfo.spawnLocation.y, worldInfo.spawnLocation.z);
                //world.setViewDistance(PluginOptions.Options().ViewDistance);
                //world.setSimulationDistance(PluginOptions.Options().SimulationDistance);
                world.getWorldBorder().setSize(PluginConfig.Options().WorldBorder);
                worldInfo.gamerules.SetToWorld(worldInfo.world);
            }

            return world;
        }
        else {
            Print.error("Error to load world! " +worldInfo.playerOwnerDisplayName + " " +worldInfo.uuid);
        }
        return null;
    }

    //Create world
    public static boolean internal_CreatePlayerWorld(String playername)
    {
        UUID uuid;
        if(PluginConfig.Options().worldstoragemethod == PluginConfig.Worldsstoragemethod.LOCAL)
        {
            uuid = UUID.randomUUID();
            String worldDir = PluginConfig.Options().WorldTemplateDirectoryZIP;
            String newName = "GPrivateWorlds/local/worlds/" + uuid + ".zip";
            try {
                FileUtils.copyFile(new File(worldDir), new File(newName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            uuid = UUID.randomUUID();
            String worldDir = PluginConfig.Options().WorldTemplateDirectoryZIP;
            String newName = "GPrivateWorlds/multinode/temp/" + uuid + ".zip";
            try {
                FileUtils.copyFile(new File(worldDir), new File(newName));
                Main.Singleton().queue.ftp.Load(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        WorldInfo worldInfo = new WorldInfo();
        LocalDate localDate = LocalDate.now();
        worldInfo.options.SetDefult();

        Internal.PlayerInfo p = Utils.AsyncGetPlayerInfoWithName(playername);

        worldInfo.uuid = uuid;
        worldInfo.playerOwnerUID = p.playerUID;
        worldInfo.playerOwnerDisplayName = playername;
        worldInfo.creationDate.day = (short)localDate.getDayOfMonth();
        worldInfo.creationDate.month = (short)localDate.getMonthValue();
        worldInfo.creationDate.year = (short)localDate.getYear();

        worldInfo.spawnLocation.set(
                PluginConfig.Options().spawnLocation.x,
                PluginConfig.Options().spawnLocation.y,
                PluginConfig.Options().spawnLocation.z);


        worldInfo.checkSum = Main.Singleton().templatechecksum;

        SaveLoadData.SaveWorldInfo(worldInfo);
        p.WorldUUID = uuid;
        SaveLoadData.UpdatePlayerInfo(p);

        return true;
    }

    public static void SaveWorld(WorldInfo worldInfo)
    {
        World world = Bukkit.getWorld("world");
        for (Player player : worldInfo.world.getPlayers())
        {
            if(world != null)
            {
                player.teleport(world.getSpawnLocation());
            }
            else
            {
                player.kickPlayer("World closed");
            }
        }

        boolean b = Bukkit.unloadWorld(worldInfo.world, true);

        if(b)
        {
            Print.info("World " + worldInfo.uuid + " unloaded");
            try
            {
                if(PluginConfig.Options().worldstoragemethod == PluginConfig.Worldsstoragemethod.LOCAL) {
                    String path = "GPrivateWorlds/local/temp/" + worldInfo.uuid + "/" ;
                    worldInfo.checkSum = Checksum.Get(path + "region");
                    SaveLoadData.UpdateWorldInfo(worldInfo);
                    Zip.zip(path, "GPrivateWorlds/local/worlds/" + worldInfo.uuid + ".zip");
                }
                else
                {
                    String path = "GPrivateWorlds/multinode/temp/" + worldInfo.uuid + "/";
                    worldInfo.checkSum = Checksum.Get(path + "region");
                    SaveLoadData.UpdateWorldInfo(worldInfo);
                    Zip.zip(path, path + ".zip");
                    Main.Singleton().queue.ftp.Load(worldInfo.uuid);
                    Print.debug("Load to FTP server");
                }
            } catch (Exception e) {
                Print.error("Error saving world " + e.getMessage());
            }
        }
    }

    public static void saveall()
    {
        for (WorldInfo worldInfo : Main.Singleton().queue.loadedworlds)
        {
            SaveWorld(worldInfo);
        }
    }

    public static class PlayerInfo
    {
        public String playerName;
        public UUID playerUID;
        public UUID WorldUUID;

        public PlayerInfo(String playerName, UUID uuid, UUID WorldUUID)
        {
            this.playerName = playerName;
            this.playerUID = uuid;
            this.WorldUUID = WorldUUID;
        }
    }

    public static class WorldInfo
    {
        public World world;
        public UUID uuid;
        public UUID playerOwnerUID;
        public String playerOwnerDisplayName;
        public String checkSum = "777";
        public CreationDate creationDate = new CreationDate();
        public List<UUID> members = new ArrayList<>();
        public SpawnLocation spawnLocation = new SpawnLocation();
        public Options options = new Options();
        public Gamerules gamerules = new Gamerules();
        public Instant lastPlayer = Instant.now();
    }

    public static WorldInfo getWorldInfo(UUID WorldUUID)
    {
        for(WorldInfo world : Main.Singleton().queue.loadedworlds)
        {
            if(world.uuid.equals(WorldUUID))
            {
                Print.debug("Ready loaded");
                return world;
            }
        }

        WorldInfo a = SaveLoadData.loadWorldInfo(WorldUUID);
        if(a == null) Print.debug("WorldInfo is null");
        return a;
    }

    public static WorldInfo GetLoadedWorldInfo(World world)
    {
        for(WorldInfo worldInfo : Main.Singleton().queue.loadedworlds) {
            if(worldInfo.world.equals(world)) {
                return worldInfo;
            }
        }
        return null;
    }
}
