package org.giancpz.gprivateworlds.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.giancpz.gprivateworlds.Internal;
import org.giancpz.gprivateworlds.Print;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class tab implements TabExecutor
{
    private List<String> subcommands = new ArrayList<>(Arrays.asList("create", "visit", "invite", "accept", "home", "kick", "leave", "setspawn"));

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(args[0].equals("visit") || args[0].equals("invite") || args[0].equals("accept"))
        {
            List<String> list = new ArrayList<>();

            for(Player player : Bukkit.getServer().getOnlinePlayers())
            {
                list.add(player.getName());
            }
            return list;
        }

        if(args[0].equals("kick"))
        {
            if(sender instanceof Player)
            {
                Player player = (Player) sender;
                Internal.WorldInfo worldInfo = Internal.GetLoadedWorldInfo(player.getWorld());

                List<String> list = new ArrayList<>();

                if(worldInfo.members.isEmpty())
                {
                    return list;
                }

                for (UUID uuid : worldInfo.members)
                {
                    Print.debug(uuid.toString());
                    list.add(Bukkit.getOfflinePlayer(uuid).getName());
                }
                return list;
            }
        }

        if(args[0].equals("create") || args[0].equals("home"))
        {
            List<String> list = new ArrayList<>();
            return list;
        }
        return subcommands;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }
}
