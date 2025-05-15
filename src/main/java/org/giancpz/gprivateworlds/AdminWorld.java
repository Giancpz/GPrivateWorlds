package org.giancpz.gprivateworlds;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static org.giancpz.gprivateworlds.Internal.*;

public class AdminWorld
{
    public static void AddMember(WorldInfo WorldInfo, PlayerInfo PlayerInfo)
    {
        WorldInfo.members.add(PlayerInfo.playerUID);
        PlayerInfo.WorldUUID = WorldInfo.uuid;
        SaveLoadData.UpdateWorldInfo(WorldInfo);
        SaveLoadData.UpdatePlayerInfo(PlayerInfo);
    }

    public static void AsyncDeleteMember(Player owner, String member)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(Utils.IsPlayerWorld(owner.getWorld()))
                {
                    if (Utils.IsOwner(owner, owner.getWorld()))
                    {
                        Internal.PlayerInfo playerInfo = Utils.AsyncGetPlayerInfoWithName(owner.getName());
                        Internal.WorldInfo worldInfo = Internal.getWorldInfo(playerInfo.WorldUUID);
                        Internal.PlayerInfo playerInfo1 = Utils.AsyncGetPlayerInfoWithName(member);
                        worldInfo.members.remove(playerInfo1.playerUID);
                        playerInfo1.WorldUUID = null;
                        SaveLoadData.UpdateWorldInfo(worldInfo);
                        SaveLoadData.UpdatePlayerInfo(playerInfo1);
                        String[] args = {member};
                        Messages.Send(owner, Messages.Get().worldMemberDelete, args);
                    }
                }
            }
        }.runTaskAsynchronously(Main.Singleton());
    }

    public static void AsyncDeleteWorld(Player player)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                PlayerInfo playerInfo = Utils.AsyncGetPlayerInfoWithName(player.getName());

                if(Utils.AsyncHasWorld(playerInfo))
                {
                    WorldInfo worldInfo = Internal.getWorldInfo(playerInfo.WorldUUID);

                    if (worldInfo.playerOwnerUID.equals(playerInfo.playerUID))
                    {
                        Bukkit.getScheduler().runTask(Main.Singleton(), () -> {
                            World world = Bukkit.getWorld("world");
                            if (world != null) {
                                if (worldInfo.world != null) {
                                    for (Player p : worldInfo.world.getPlayers()) {
                                        if (world == null) p.kickPlayer("You couldn't go to world, error 555");
                                        else p.teleport(world.getSpawnLocation());
                                    }
                                }
                            }
                            if (worldInfo.world != null) Bukkit.unloadWorld(worldInfo.world, false);
                        });

                        playerInfo.WorldUUID = null;
                        SaveLoadData.UpdatePlayerInfo(playerInfo);
                        for (UUID member : worldInfo.members) {
                            Internal.PlayerInfo playerInfo1 = Utils.AsyncGetPlayerInfo(member);
                            playerInfo1.WorldUUID = null;
                            SaveLoadData.UpdatePlayerInfo(playerInfo1);
                        }
                        SaveLoadData.DeleteWorldInfo(worldInfo);
                    } else {
                        Messages.Send(player, Messages.Get().worldNotOwn, null);
                        //PlayerMessage.Send(player, "You do not own this world", PlayerMessage.MessageType.ERROR);
                    }
                }
                else {
                    Messages.Send(player, Messages.Get().worldNotFoundPlayer, null);
                }
            }
        }.runTaskAsynchronously(Main.Singleton());
    }

    public static void AsyncLeave(Player player)
    {
        PlayerInfo playerInfo = Utils.AsyncGetPlayerInfoWithName(player.getName());

        if(Utils.AsyncHasWorld(playerInfo))
        {
            WorldInfo worldInfo = Internal.getWorldInfo(playerInfo.WorldUUID);

            if (!worldInfo.playerOwnerUID.equals(playerInfo.playerUID)) {
                worldInfo.members.remove(playerInfo.playerUID);
                playerInfo.WorldUUID = null;
                SaveLoadData.UpdateWorldInfo(worldInfo);
                SaveLoadData.UpdatePlayerInfo(playerInfo);
                Messages.Send(player, Messages.Get().worldLeavePlayer, null);
                //PlayerMessage.Send(player, "You left the world", PlayerMessage.MessageType.INFO);
            } else {
                Messages.Send(player, Messages.Get().worldNotLeave, null);
                //PlayerMessage.Send(player, "You own the world", PlayerMessage.MessageType.ERROR);
            }
        }
    }

    public static void SetSpawn(Player player) {
        if (Utils.IsPlayerWorld(player.getWorld()))
        {
            WorldInfo worldInfo = Internal.GetLoadedWorldInfo(player.getWorld());
            if (worldInfo != null) {
                if (Utils.IsOwner(player, worldInfo.world))
                {
                    worldInfo.spawnLocation.set(
                            player.getLocation().getBlockX(),
                            player.getLocation().getBlockY(),
                            player.getLocation().getBlockZ());
                    worldInfo.world.setSpawnLocation(
                            worldInfo.spawnLocation.x,
                            worldInfo.spawnLocation.y,
                            worldInfo.spawnLocation.z);

                    Messages.Send(player, Messages.Get().worldSetSpawn, null);
                } else {
                    Messages.Send(player, Messages.Get().worldNotOwn, null);
                    //PlayerMessage.Send(player, "You do not own this world", PlayerMessage.MessageType.ERROR);
                }
                SaveLoadData.UpdateWorldInfo(worldInfo);
            }
        }
    }
}
