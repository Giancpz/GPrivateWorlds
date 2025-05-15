package org.giancpz.gprivateworlds.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.giancpz.gprivateworlds.Internal;
import org.giancpz.gprivateworlds.Main;
import org.giancpz.gprivateworlds.Print;
import org.giancpz.gprivateworlds.Utils;
import java.util.ArrayList;
import java.util.List;

public class VisitorsMenu extends GUI
{
    public VisitorsMenu(GUIManager.MenuType menuType) {
        super(menuType);
    }

    @Override
    public void onItem(Player player, int slot, ItemStack item, GUIManager.Menu menu) {
        Main.Singleton().joinManager.Invite(player, menu.cache.get(slot));
        Open(player);
    }

    @Override
    public GUIManager.Menu Open(Player player)
    {
        GUIManager.Menu menu = super.Open(player);

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
        return menu;
    }
}
