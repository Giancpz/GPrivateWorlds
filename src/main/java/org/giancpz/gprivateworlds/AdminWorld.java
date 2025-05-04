package org.giancpz.gprivateworlds;
import org.bukkit.entity.Player;

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

    public static void SetSpawn(Player player)
    {
        WorldInfo worldInfo = Internal.GetLoadedWorldInfo(player.getWorld());
        if(worldInfo != null)
        {
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
            }
            else
            {
                player.sendMessage("You do not own this world");
            }
            SaveLoadData.UpdateWorldInfo(worldInfo);
        }
    }
}
