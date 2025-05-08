package org.giancpz.gprivateworlds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUI
{
    public static List<Menu> players = new ArrayList<>();
    public static void OpenWorldMenu(Player player)
    {
        if(Utils.IsPlayerWorld(player.getWorld()))
        {
            if(Utils.IsOwner(player, player.getWorld()))
            {
                Internal.WorldInfo worldInfo = Internal.GetLoadedWorldInfo(player.getWorld());

                Inventory inv = Bukkit.createInventory(null, 54, "World information");
                ItemStack members = new ItemStack(Material.BOOK);
                {
                ItemMeta meta = members.getItemMeta();

                    if (meta != null) {
                        meta.setDisplayName(ChatColor.GREEN + "Members");
                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.GRAY + "Click to manage members");
                        meta.setLore(lore);
                        members.setItemMeta(meta);
                    }
                }

                ItemStack visitors = new ItemStack(Material.PAPER);
                {
                    ItemMeta meta = visitors.getItemMeta();

                    if (meta != null) {
                        meta.setDisplayName(ChatColor.GREEN + "Visitors");
                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.GRAY + "Click to manage visitors");
                        meta.setLore(lore);
                        visitors.setItemMeta(meta);
                    }
                }

                ItemStack visibility = new ItemStack(Material.ENDER_PEARL);
                {
                    ItemMeta meta2 = visibility.getItemMeta();
                    if (meta2 != null) {
                        meta2.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Visibility");
                        List<String> lore = new ArrayList<>();

                        if(worldInfo.options.Visibility) lore.add(ChatColor.GREEN + "PUBLIC");
                            else lore.add(ChatColor.RED + "PRIVATE");

                        meta2.setLore(lore);
                        visibility.setItemMeta(meta2);
                    }
                }

                inv.setItem(12, members);
                inv.setItem(14, visibility);
                inv.setItem(30, visitors);

                Menu.add(player, MenuType.WORLD_MENU);
                player.openInventory(inv);
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You do not own this world");
            }
        }
    }

    public static void OpenMembersMenu(Player player)
    {
        if(Utils.IsPlayerWorld(player.getWorld()))
        {
            if(Utils.IsOwner(player, player.getWorld()))
            {
                Menu menu = Menu.add(player, MenuType.MEMBERS_MENU);
                Internal.WorldInfo worldInfo = Internal.GetLoadedWorldInfo(player.getWorld());
                Print.debug(worldInfo.members.size() + " members");
                Inventory inv = Bukkit.createInventory(null, 54, "Members");

                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        int index = 0;
                        for (UUID member : worldInfo.members)
                        {
                            Internal.PlayerInfo p = Utils.AsyncGetPlayerInfo(member);
                            Print.debug(p.playerName);
                            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                            SkullMeta meta = (SkullMeta)skull.getItemMeta();
                            if (meta != null) {
                                meta.setDisplayName(p.playerName);
                                List<String> lore = new ArrayList<>();
                                lore.add(ChatColor.GRAY + "Click to remove");
                                meta.setLore(lore);
                                skull.setItemMeta(meta);
                            }
                            menu.cache.add(p.playerName);
                            inv.setItem(index, skull);
                            index++;
                        }
                    }
                }.runTaskAsynchronously(Main.Singleton());

                Print.debug("Open members menu");
                player.openInventory(inv);
            }
            else
            {
                player.sendMessage(ChatColor.RED + "No eres dueño de este mundo");
            }
        }
    }

    public static void OpenVisitors(Player player)
    {
        Menu menu = Menu.add(player, MenuType.VISITORS_MENU);

        Inventory inv = Bukkit.createInventory(null, 54, "World information");
        int index = 0;
        Internal.WorldInfo wInfo = Internal.GetLoadedWorldInfo(player.getWorld());

        for(Player p : player.getWorld().getPlayers())
        {
            if(!Utils.IsMember(p, wInfo))
            {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta meta = (SkullMeta)skull.getItemMeta();
                if (meta != null)
                {
                    meta.setDisplayName(p.getName());
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Click to invite");
                    meta.setLore(lore);
                    skull.setItemMeta(meta);
                }
                menu.cache.add(p.getName());
                inv.setItem(index, skull);
                index++;
            }
            else
            {
                Print.debug("Es miembro");
            }
        }
        player.openInventory(inv);
    }

    public static void OnItem(Player player, ItemStack item, int slot)
    {
        Menu menu =  Menu.GetMenu(player);
        if(menu == null) return;

        if(menu.menuType == MenuType.WORLD_MENU)
        {
            switch (slot)
            {
                case 14:
                    Internal.WorldInfo worldInfo = Internal.GetLoadedWorldInfo(player.getWorld());
                    worldInfo.options.Visibility = !worldInfo.options.Visibility;
                    OpenWorldMenu(player);
                    return;
                case 12:
                    OpenMembersMenu(player);
                    return;
                case 30:
                    OpenVisitors(player);
                    return;
            }
        }

        if(menu.menuType == MenuType.MEMBERS_MENU) {
            AdminWorld.AsyncDeleteMember(player, menu.cache.get(slot));
            OpenMembersMenu(player);
            return;
        }

        if(menu.menuType == MenuType.VISITORS_MENU)
        {
            Main.Singleton().joinManager.Invite(player, menu.cache.get(slot));
            OpenVisitors(player);
        }
    }

    public enum MenuType
    {
        WORLD_MENU,
        WORLD_OPTIONS,
        USER_MENU,
        MEMBERS_MENU,
        VISITORS_MENU
    }

    public static class Menu
    {
        Player player;
        MenuType menuType;
        List<String> cache = new ArrayList<>();

        public static boolean OnMenu(Player player)
        {
            for (Menu m : players)
            {
                if(m.player.equals(player)) return true;
            }
            return false;
        }

        public static Menu add(Player player, MenuType menu)
        {
            Menu me = new Menu();
            me.player = player;
            me.menuType = menu;
            players.add(me);
            return me;
        }

        public static Menu GetMenu(Player player)
        {
            for (Menu m : players) if(m.player.equals(player)) return m;
            return null;
        }

        public static void remove(Player player)
        {
            Menu m0 = null;
            for (Menu m : players)
            {
                if(m.player.equals(player))
                {
                    m0 = m;
                    break;
                }
            }
            Print.debug("del menu");
            players.remove(m0);
        }
    }
}
