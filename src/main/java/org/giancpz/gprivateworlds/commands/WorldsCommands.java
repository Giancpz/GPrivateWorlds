package org.giancpz.gprivateworlds.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.giancpz.gprivateworlds.Internal;
import org.giancpz.gprivateworlds.JoinManager;
import org.giancpz.gprivateworlds.Main;
import org.jetbrains.annotations.NotNull;

public class WorldsCommands implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings)
    {
        if(strings.length > 0) {
            if(commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (strings[0].equals("create")) {
                    Main.Singleton().node.CreateWorld(player.getName());
                }

                if (strings[0].equals("home")) {
                    Main.Singleton().node.TeleportToWorld(player.getName(), player.getName());
                }

                if (strings[0].equals("visit")) {
                    if (strings.length > 1) {
                        Main.Singleton().node.TeleportToWorld(player.getName(), strings[1]);
                    }
                }

                if (strings[0].equals("invite")) {
                    if (strings.length > 1) {
                        JoinManager.AddTask(JoinManager.Task.taskType.INVITE, player.getName(), strings[1]);
                    }
                }

                if (strings[0].equals("accept")) {
                    if (strings.length > 1) {
                        JoinManager.AddTask(JoinManager.Task.taskType.ACCEPT, player.getName(), strings[1]);
                        return true;
                    }
                    else {
                        JoinManager.AddTask(JoinManager.Task.taskType.ACCEPT, player.getName(), null);
                    }
                    //Main.Singleton().joinManagerOld.Accept(player, null);
                }
            }
            else
            {
                if (strings[0].equals("reload")) {
                    Internal.saveall();
                }
            }
            return true;
        }
        return false;
    }
}
