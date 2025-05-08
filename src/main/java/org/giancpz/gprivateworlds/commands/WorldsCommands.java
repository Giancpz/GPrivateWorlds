package org.giancpz.gprivateworlds.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.giancpz.gprivateworlds.*;
import org.jetbrains.annotations.Async;
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

                if(strings[0].equals("menu"))
                {
                    GUI.OpenWorldMenu(player);
                    return true;
                }

                if (strings[0].equals("home")) {
                    Main.Singleton().node.TeleportToWorld(player.getName(), player.getName());
                    return true;
                }

                if (strings[0].equals("visit")) {
                    if (strings.length > 1) {
                        Main.Singleton().node.TeleportToWorld(player.getName(), strings[1]);
                        return true;
                    }
                }

                if (strings[0].equals("setspawn")) {
                    AdminWorld.SetSpawn(player);
                    return true;
                }

                if (strings[0].equals("invite")) {
                    if (strings.length > 1) {
                        Main.Singleton().joinManager.Invite(player, strings[1]);
                        return true;
                    }
                }

            // TEMPORAL
                if (strings[0].equals("kick")) {
                    if (strings.length > 1) {
                        Internal.AddManagerTask(null, player.getName(), strings[1], Queue.TaskInfo.TaskType.DELETE_PLAYER, "");
                        return true;
                    }
                }

                // TEMPORAL
                if (strings[0].equals("delete"))
                {
                    if (strings.length > 1)
                    {
                        if(strings[1].equals("confirm"))
                        {
                            AdminWorld.AsyncDeleteWorld(player);
                        }
                    }
                    else
                    {
                        PlayerMessage.Send(player, "This command will delete your world forever!", PlayerMessage.MessageType.ERROR);
                        PlayerMessage.Send(player, "Use /pw delete confirm", PlayerMessage.MessageType.ERROR);
                    }
                }
            // TEMPORAL
                if (strings[0].equals("leave")) {
                    AdminWorld.AsyncLeave(player);
                }

                if (strings[0].equals("accept")) {
                    if (strings.length > 1) {
                        Main.Singleton().joinManager.Accept(player, strings[1]);
                        return true;
                    }
                    else {
                        Main.Singleton().joinManager.Accept(player,null);
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
