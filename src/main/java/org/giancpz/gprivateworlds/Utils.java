package org.giancpz.gprivateworlds;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.giancpz.gprivateworlds.Internal.WorldInfo;

import java.util.UUID;

public class Utils
{
    public static boolean IsMember(Player player, World world) {
        if (IsOwner(player, world))
        {
            return true;
        }

        WorldInfo w = Internal.GetLoadedWorldInfo(world);

        if (w != null && w.world.equals(world)) {
            return w.members.contains(player.getUniqueId());
        }

        return false;
    }

    public static boolean IsMember(Player player, WorldInfo worldInfo)
    {
        if(worldInfo.playerOwnerUID.equals(player.getUniqueId()))
        {
            return true;
        }

        return worldInfo.members.contains(player.getUniqueId());
    }

    public static Boolean IsOwner(Player player, World world)
    {
        WorldInfo w = Internal.GetLoadedWorldInfo(world);

        if (w != null && w.world.equals(world)) {
            return w.playerOwnerUID.equals(player.getUniqueId());
        }
        return false;
    }

    public static boolean AsyncIsOwner(String player)
    {
        Internal.PlayerInfo p = AsyncGetPlayerInfoWithName(player);
        Internal.WorldInfo w = Internal.getWorldInfo(p.WorldUUID);

        return w.playerOwnerUID.equals(p.playerUID);
    }

    public static boolean AsyncHasWorld(String player)
    {
        Internal.PlayerInfo playerInfo = AsyncGetPlayerInfoWithName(player);

        return playerInfo != null && playerInfo.WorldUUID != null;
    }

    public static boolean AsyncHasWorld(Internal.PlayerInfo player)
    {
        return player.WorldUUID != null;
    }

    public static boolean IsPlayerWorld(World world)
    {
        WorldInfo w =  Internal.GetLoadedWorldInfo(world);
        return w != null;
    }

    public static Internal.PlayerInfo AsyncGetPlayerInfo(UUID playerUID)
    {
        return SaveLoadData.loadPlayerInfo(playerUID.toString());
    }

    public static Internal.PlayerInfo AsyncGetPlayerInfoWithName(String playerName)
    {
        return SaveLoadData.loadPlayerInfoWithName(playerName);
    }

    /*
    public static Internal.PlayerInfo AsyncGetPlayerInfo(Player player)
    {
        return SaveLoadData.loadPlayerInfo(player.getUniqueId().toString());
    }
    */

    public static String WorldUUIDToString(UUID uuid)
    {
        if(uuid == null) return "none";
        return uuid.toString();
    }

    public static UUID WorldStringToUUId(String string)
    {
        if(string.equals("none")) return null;
        return UUID.fromString(string);
    }
}
