package org.giancpz.gprivateworlds.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.giancpz.gprivateworlds.*;

import java.util.ArrayList;
import java.util.List;

public class AllWorldsMenu extends GUI
{
    public AllWorldsMenu(GUIManager.MenuType menuType) {
        super(menuType);
    }

    @Override
    public void onItem(Player player, int slot, ItemStack item, GUIManager.Menu menu) {
        super.onItem(player, slot, item, menu);
    }

    @Override
    public GUIManager.Menu Open(Player player) {
        GUIManager.Menu menu = super.Open(player);
        Inventory inv = Bukkit.createInventory(null, 54, "Worlds");
        player.openInventory(inv);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                List<String> worlds = SaveLoadData.FindWorlds(0,20);

                int index = 0;
                for(String world : worlds)
                {
                    ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                    SkullMeta meta = (SkullMeta)skull.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(world);
                        List<String> lore = new ArrayList<>();
                        lore.add(ChatColor.GRAY + "Click to remove");
                        meta.setLore(lore);
                        skull.setItemMeta(meta);
                    }
                    inv.setItem(index, skull);
                    index++;
                }
            }
        }.runTaskAsynchronously(Main.Singleton());

        return menu;
    }
}
