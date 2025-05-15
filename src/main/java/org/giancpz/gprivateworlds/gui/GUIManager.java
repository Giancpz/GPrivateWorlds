package org.giancpz.gprivateworlds.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.giancpz.gprivateworlds.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GUIManager
{
    public static LinkedList<Menu> players = new LinkedList<>();

    public static WorldMenu worldMenu = new WorldMenu(MenuType.WORLD_MENU);
    public static MemberMenu memberMenu = new MemberMenu(MenuType.MEMBERS_MENU);
    public static VisitorsMenu visitorsMenu = new VisitorsMenu(MenuType.VISITORS_MENU);
    public static AllWorldsMenu allWorldsMenu = new AllWorldsMenu(MenuType.ALLWORLDS_MENU);

    public static void OnItem(Player player, ItemStack item, int slot)
    {
        Menu menu =  Menu.GetMenu(player);

        if(menu == null) return;

        if(menu.menuType == MenuType.WORLD_MENU)
        {
            worldMenu.onItem(player,slot, item, menu);
            return;
        }

        if(menu.menuType == MenuType.MEMBERS_MENU) {
            memberMenu.onItem(player,slot, item, menu);
            return;
        }

        if(menu.menuType == MenuType.VISITORS_MENU)
        {
            visitorsMenu.onItem(player,slot, item, menu);
        }
    }

    public enum MenuType
    {
        WORLD_MENU,
        MEMBERS_MENU,
        VISITORS_MENU,
        ALLWORLDS_MENU
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
            //Menu m = GetMenu(player);
            Menu me = new Menu();
            me.player = player;
            me.menuType = menu;
            //if(m != null) me.cache = m.cache;
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
