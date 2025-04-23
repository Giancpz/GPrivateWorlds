package org.giancpz.gprivateworlds;
import static org.giancpz.gprivateworlds.Internal.*;

public class AdminWorld
{
    public static void AddMember(WorldInfo WorldInfo, PlayerInfo PlayerInfo)
    {
        WorldInfo.members.add(PlayerInfo.playerUID);
        PlayerInfo.WorldUUID = WorldInfo.uuid;
    }
}
