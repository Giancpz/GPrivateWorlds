package org.giancpz.gprivateworlds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerMessage
{
    public enum MessageType{
        INFO,
        ERROR
    }

    public static void Send(Player to, String message,MessageType messageType)
    {
        Internal_Send(null, to, message,messageType,null);
    }

    public static void Send(String to, String message,MessageType messageType, String nodename)
    {
        Internal_Send(to, null ,message, messageType, nodename);
    }

    private static void Internal_Send(String toName, Player toPlayer, String message,MessageType messageType, String nodename)
    {
        if(nodename == null)
        {
            if(PluginConfig.Options().nodemode == PluginConfig.Nodemode.LOCAL || PluginConfig.Options().nodemode == PluginConfig.Nodemode.MAIN)
            {
                Player player = null;

                Print.debug(toName);

                if(toName != null) {
                    player = Bukkit.getPlayer(toName);
                } else {
                    player = toPlayer;
                }

                if(player != null) {
                    switch(messageType) {
                        case INFO:
                            player.sendMessage(ChatColor.GREEN +  message);
                            break;
                        case ERROR:
                            player.sendMessage(ChatColor.RED +  message);
                    }
                }
            }
        }
        else
        {
            Main.Singleton().node.Send(nodename, "notify-player:" + toName + ":" + message);
        }
    }
}
