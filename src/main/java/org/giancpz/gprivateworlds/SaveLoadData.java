package org.giancpz.gprivateworlds;

import org.giancpz.gprivateworlds.Internal.PlayerInfo;
import org.giancpz.gprivateworlds.Internal.WorldInfo;

import java.util.UUID;

public class SaveLoadData
{
    public static final String PLAYER_UUID_1 = "player_uuid";
    public static final String PLAYER_NAME_2 = "player_name";
    public static final String MEMBER_UUID_3 = "member_uuid";

    public static final String WORLD_UUID_1 = "world_uuid";
    public static final String OWNER_UUID_2 = "owner_uuid";
    public static final String OWNER_NAME_3 = "owner_name";
    public static final String MEMBERS_UUID_4 = "members_uuid";
    public static final String SPAWN_LOCATION_5 = "spawn_location";
    public static final String CREATION_DATE_6 = "creation_date";
    public static final String OPTIONS_7 = "options";
    public static final String CHECKSUM_8 = "checksum";

    public static WorldInfo loadWorldInfo(UUID worldUUID) {
        return Main.Singleton().db.FindWorldByUUID(worldUUID);
    }

    public static void SaveWorldInfo(WorldInfo worldInfo) {
        Main.Singleton().db.InsertWorld(worldInfo);
    }

    public static void UpdateWorldInfo(WorldInfo worldInfo)
    {
        Main.Singleton().db.UpdateWorld(worldInfo);
    }

    public static void DeleteWorldInfo(WorldInfo worldInfo)
    {
        Main.Singleton().db.DeleteWorldInfo(worldInfo);
    }

    public static PlayerInfo loadPlayerInfo(String playerUID) {
        return Main.Singleton().db.FindPlayerByUUID(playerUID);
    }

    public static PlayerInfo loadPlayerInfoWithName(String playerName) {
        return Main.Singleton().db.FindPlayerByName(playerName);
    }

    public static void savePlayerInfo(PlayerInfo playerInfo) {
        Main.Singleton().db.InsertPlayer(playerInfo);
    }

    public static void UpdatePlayerInfo(PlayerInfo playerInfo)
    {
        Main.Singleton().db.UpdatePlayer(playerInfo);
    }
}
