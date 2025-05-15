package org.giancpz.gprivateworlds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;
import org.giancpz.gprivateworlds.gui.GUIManager;

import java.time.Instant;

public class mListener implements Listener
{
    @EventHandler(priority= EventPriority.HIGHEST)
    public void worldInit(WorldInitEvent e)
    {
        World w = e.getWorld();
        w.setKeepSpawnInMemory(false);
        //w.setGameRule(GameRule.,0);
    }

    @EventHandler
    public void OnWorldJoin(PlayerChangedWorldEvent e)
    {
        Print.warning(e.getPlayer().getDisplayName());

        World world = e.getPlayer().getWorld();
        if(Utils.IsPlayerWorld(world))
        {
            Internal.WorldInfo worldInfo = Internal.GetLoadedWorldInfo(world);
            if(worldInfo != null)
            {
                worldInfo.lastPlayer = Instant.now();
            }
        }
    }

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent e)
    {
        Print.debug(e.getUniqueId().toString());
        Internal.PlayerInfo p = Utils.AsyncGetPlayerInfo(e.getUniqueId());

        if(p != null) {
            if(!p.playerName.equals(e.getName())) {
                Print.debug("Player change name");
            }
        } else {
            Internal.PlayerInfo newplayer = new Internal.PlayerInfo(e.getName(), e.getUniqueId(), null);
            SaveLoadData.savePlayerInfo(newplayer);
            Print.debug("New player added");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();

        p.teleport(p.getWorld().getSpawnLocation());

        for (Queue.TeleportQueue q : Main.Singleton().queue.teleportQueue)
        {
            if(q.PlayerName.equals(p.getName()))
            {
                p.teleport(q.toWorld.getSpawnLocation());
            }
        }
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
        Player p = e.getPlayer();
        if(Utils.IsPlayerWorld(p.getWorld())) {
            World w = p.getWorld();
            if (p.getLocation().getY() < PluginConfig.Options().VoidTeleport) {
                Location loc = w.getSpawnLocation();
                Block block = w.getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());

                if (block.isEmpty() || block.isLiquid()) {
                    block.setType(Material.GLASS);
                }
                p.setFallDistance(0);
                p.teleport(w.getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void OnPlayerPlaceBlock(BlockPlaceEvent e) {
        if (Utils.IsPlayerWorld(e.getBlock().getWorld())) {
            Player p = e.getPlayer();
            World w = p.getWorld();

            if (!Utils.IsMember(p, w)) {
                e.setCancelled(true);
            }

            if (
                e.getBlock().getX() > PluginConfig.Options().WorldSizeX ||
                e.getBlock().getZ() > PluginConfig.Options().WorldSizeZ ||
                e.getBlock().getX() < -PluginConfig.Options().WorldSizeX ||
                e.getBlock().getZ() < -PluginConfig.Options().WorldSizeZ ||
                e.getBlock().getY() > PluginConfig.Options().WorldSizeY ||
                e.getBlock().getY() < PluginConfig.Options().WorldSizeNY )
            {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void OnPlayerBreakBlock(BlockBreakEvent e)
    {
        if(!Utils.IsPlayerWorld(e.getBlock().getWorld()))
        {
            return;
        }

        if(!Utils.IsMember(e.getPlayer(),e.getBlock().getWorld()))
        {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void OnPlayerInteracEvent(PlayerInteractEvent e)
    {
        if(Utils.IsPlayerWorld(e.getPlayer().getWorld()))
        {
            if(!Utils.IsMember(e.getPlayer(),e.getPlayer().getWorld()))
            {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void OnEventtest(EntityTargetEvent e)
    {
        if (!Utils.IsPlayerWorld(e.getEntity().getWorld())) return;

        if (e.getTarget() instanceof Player) {
            Player p = (Player) e.getTarget();
            if (!Utils.IsMember(p, p.getWorld())) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void OnEntityDamage(EntityDamageByEntityEvent e)
    {
        if(Utils.IsPlayerWorld(e.getDamager().getWorld()) || Utils.IsPlayerWorld(e.getEntity().getWorld()))
        {
            if (e.getDamager().getType() == EntityType.PLAYER)
            {
                Player p = (Player) e.getDamager();
                if (!Utils.IsMember(p, p.getWorld())) {
                    e.setCancelled(true);
                }
            }

            if (e.getEntity().getType() == EntityType.PLAYER)
            {
                Player p = (Player) e.getEntity();
                if (!Utils.IsMember(p, p.getWorld())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if(e.getWhoClicked() instanceof Player)
        {
            if (GUIManager.Menu.OnMenu(Bukkit.getPlayer(e.getWhoClicked().getUniqueId())))
            {
                e.setCancelled(true);
                ItemStack clicked = e.getCurrentItem();
                if (clicked == null || clicked.getType() == Material.AIR) return;
                Player p = (Player) e.getWhoClicked();
                GUIManager.OnItem(p, clicked ,e.getSlot());
            }
        }
    }

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent e)
    {
        if(e.getPlayer() instanceof Player)
        {
            GUIManager.Menu.remove(Bukkit.getPlayer(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent e)
    {
        GUIManager.Menu.remove(e.getPlayer());
    }
}
