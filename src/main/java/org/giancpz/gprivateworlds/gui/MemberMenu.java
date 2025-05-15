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
import java.util.UUID;

public class MemberMenu extends GUI
{
    public MemberMenu(GUIManager.MenuType menuType) {
        super(menuType);
    }

    @Override
    public void onItem(Player player, int slot , ItemStack item, GUIManager.Menu menu)
    {
        AdminWorld.AsyncDeleteMember(player, menu.cache.get(slot));
        Open(player);
    }
    @Override
    public GUIManager.Menu Open(Player player)
    {
        if(Utils.IsPlayerWorld(player.getWorld()))
        {
            if(Utils.IsOwner(player, player.getWorld()))
            {
                GUIManager.Menu menu = super.Open(player);
                menu.cache.clear();

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
                return menu;
            }
            else
            {
                player.sendMessage(ChatColor.RED + "No eres dueño de este mundo");
            }
        }
        return null;
    }
}
