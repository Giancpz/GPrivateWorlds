package org.giancpz.gprivateworlds.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GUI
{
    GUIManager.MenuType menuType;

    public GUI(GUIManager.MenuType menuType)
    {
        this.menuType = menuType;
    }

    public void onItem(Player player, int slot, ItemStack item, GUIManager.Menu menu)
    {

    }

    public GUIManager.Menu Open(Player player)
    {
        return GUIManager.Menu.add(player, menuType);
    }

    public void OnEnabled(GUIManager.MenuType mt)
    {
        menuType = mt;
    }
}
