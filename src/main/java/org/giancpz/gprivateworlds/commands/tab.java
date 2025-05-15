package org.giancpz.gprivateworlds.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
import java.util.stream.Collectors;

public class tab implements TabExecutor
{
    private final List<String> subcommands = new ArrayList<>(Arrays.asList("create", "visit", "invite", "accept", "home", "kick", "leave", "setspawn", "delete"));
    private final List<String> allsubcommands = new ArrayList<>(Arrays.asList("create", "visit", "invite", "accept", "home", "kick", "leave", "setspawn", "delete", "template"));

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args)
    {
        if(sender instanceof Player)
        {
            Player p = (Player) sender;

            if (args[0].equals("sound"))
            {
                if(args.length > 1)
                {
                    List<String> listaDeColores = Arrays.stream(Sound.values())
                            .map(Enum::name)
                            .collect(Collectors.toList());


                    List<String> temp = new ArrayList<>();
                    for (String subcommand : listaDeColores) {
                        if (subcommand.startsWith(args[1])) {
                            temp.add(subcommand);
                        }
                    }
                    return temp;
                }
            }

            if (args[0].equals("visit") || args[0].equals("invite") || args[0].equals("accept")) {
                List<String> list = new ArrayList<>();

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    list.add(player.getName());
                }
                return list;
            }

            if (args[0].equals("kick"))
            {
                Player player = (Player) sender;
                Internal.WorldInfo worldInfo = Internal.GetLoadedWorldInfo(player.getWorld());

                if (worldInfo != null) {

                    List<String> list = new ArrayList<>();

                    if (worldInfo.members.isEmpty()) {
                        return list;
                    }

                    for (UUID uuid : worldInfo.members) {
                        Print.debug(uuid.toString());
                        list.add(Bukkit.getOfflinePlayer(uuid).getName());
                    }
                    return list;
                }
            }

            if (args[0].equals("create") || args[0].equals("home")) {
                List<String> list = new ArrayList<>();
                return list;
            }

            if (args[0].equals("template"))
            {
                List<String> list = new ArrayList<>();
                list.add("edit");
                list.add("save");
                list.add("setspawn");
                return list;
            }

            List<String> list;

            if(p.isOp()) list = allsubcommands;
            else list = subcommands;

            List<String> temp = new ArrayList<>();

            for (String subcommand : list)
            {
                if(subcommand.startsWith(args[0]))
                {
                    temp.add(subcommand);
                }
            }
            return temp;
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }
}
