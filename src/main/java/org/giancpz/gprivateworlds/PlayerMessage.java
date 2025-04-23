package org.giancpz.gprivateworlds;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerMessage
{
    public static void Send(String to, String message, String nodename)
    {
        if(nodename == null)
        {
            if(PluginConfig.Options().nodemode == PluginConfig.Nodemode.LOCAL ||
                PluginConfig.Options().nodemode == PluginConfig.Nodemode.MAIN)
            {
                Player player = Bukkit.getPlayer(to);
                if(player != null) player.sendMessage(message);
            }
        }
        else
        {
            Main.Singleton().node.Send(nodename, "notify-player:" + to + ":" + message);
        }
    }
}
