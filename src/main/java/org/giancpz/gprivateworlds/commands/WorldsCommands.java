package org.giancpz.gprivateworlds.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.giancpz.gprivateworlds.*;
import org.giancpz.gprivateworlds.gui.GUIManager;
import org.jetbrains.annotations.NotNull;

public class WorldsCommands implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings)
    {
        if(strings.length > 0) {
            if(commandSender instanceof Player)
            {
                Player player = (Player) commandSender;

                switch(strings[0])
                {
                    case "create":
                        Main.Singleton().node.CreateWorld(player.getName());
                        break;

                    case "menu":
                        GUIManager.worldMenu.Open(player);
                        break;

                    case "home":
                        Main.Singleton().node.TeleportToWorld(player.getName(), player.getName());
                        break;

                    case "visit":
                        if (strings.length > 1)
                            Main.Singleton().node.TeleportToWorld(player.getName(), strings[1]);
                        break;

                    case "setspawn":
                        AdminWorld.SetSpawn(player);
                        break;

                    case "template":
                        if(strings[1].equals("edit"))
                        {
                            TemplateEditor.Edit(player);
                        }

                        if(strings[1].equals("save"))
                        {
                            TemplateEditor.Save();
                        }

                        if(strings[1].equals("setspawn"))
                        {
                            TemplateEditor.SetSpawn(player);
                        }
                        break;

                    case "invite":
                        if (strings.length > 1) {
                            Main.Singleton().joinManager.Invite(player, strings[1]);
                        }
                        break;

                    case "kick":
                        if (strings.length > 1) {
                            AdminWorld.AsyncDeleteMember(player, strings[1]);
                            return true;
                        }
                        break;

                    case "delete":
                        if (strings.length > 1)
                        {
                            if(strings[1].equals("confirm"))
                            {
                                AdminWorld.AsyncDeleteWorld(player);
                            }
                        }
                        else
                        {
                            Messages.Send(player, Messages.Get().worldDeleteWarning, null);
                            //PlayerMessage.Send(player, "This command will delete your world forever!", PlayerMessage.MessageType.ERROR);
                            //PlayerMessage.Send(player, "Use /pw delete confirm", PlayerMessage.MessageType.ERROR);
                        }
                    break;

                    case "leave":
                        AdminWorld.AsyncLeave(player);
                        break;

                    case "accept":
                        if (strings.length > 1) {
                            Main.Singleton().joinManager.Accept(player, strings[1]);
                            return true;
                        }
                        else {
                            Main.Singleton().joinManager.Accept(player,null);
                        }
                    break;

                    case "reload":
                        Main.Singleton().PluginReload();
                        break;
                }
            }
            else
            {
                if (strings[0].equals("reload")) {
                    Internal.saveall();
                }

                if (strings[0].equals("resume")) {
                    Main.Singleton().queue.ResumeQueue();
                }
            }
            return true;
        }
        return false;
    }
}
