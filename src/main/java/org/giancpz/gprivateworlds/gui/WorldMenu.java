package org.giancpz.gprivateworlds.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.giancpz.gprivateworlds.Internal;
import org.giancpz.gprivateworlds.Print;
import org.giancpz.gprivateworlds.Utils;
import java.util.ArrayList;
import java.util.List;

public class WorldMenu extends GUI
{
    public WorldMenu(GUIManager.MenuType menuType) {
        super(menuType);
    }

    @Override
    public void OnEnabled(GUIManager.MenuType mt) {
        super.OnEnabled(menuType);
    }

    @Override
    public void onItem(Player player, int slot , ItemStack item, GUIManager.Menu menu)
    {
        Print.warning(String.valueOf(slot));
        switch (slot)
        {
            case 14:
                Internal.WorldInfo worldInfo = Internal.GetLoadedWorldInfo(player.getWorld());
                worldInfo.options.Visibility = !worldInfo.options.Visibility;
                Open(player);
                return;
            case 12:
                GUIManager.memberMenu.Open(player);
                return;
            case 30:
                GUIManager.visitorsMenu.Open(player);
                return;
            case 32:
                GUIManager.allWorldsMenu.Open(player);
        }
    }
    @Override
    public GUIManager.Menu Open(Player player)
    {
        if(Utils.IsPlayerWorld(player.getWorld()))
        {
            if(Utils.IsOwner(player, player.getWorld()))
            {
                Internal.WorldInfo worldInfo = Internal.GetLoadedWorldInfo(player.getWorld());

                GUIManager.Menu menu = super.Open(player);

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
                inv.setItem(32, visitors);

                player.openInventory(inv);
                return menu;
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You do not own this world");
            }
        }
        return null;
    }
}
