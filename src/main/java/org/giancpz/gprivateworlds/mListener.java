package org.giancpz.gprivateworlds;

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
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldInitEvent;

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
        if(p.getLocation().getY() < 0)
        {
            if(!Utils.IsPlayerWorld(p.getWorld())) return;

            World w = p.getWorld();
            if(Utils.IsPlayerWorld(w))
            {
                Location loc = w.getSpawnLocation();
                Block block = w.getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());

                if(block.isEmpty() || block.isLiquid()) {
                    block.setType(Material.GLASS);
                }

                p.teleport(w.getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void OnPlayerPlaceBlock(BlockPlaceEvent e) {
        if (Utils.IsPlayerWorld(e.getBlock().getWorld()))
        {
            Player p = e.getPlayer();
            World w = p.getWorld();

            if (!Utils.IsMember(p, w)) {
                e.setCancelled(true);
            }

            if (e.getBlock().getX() > PluginConfig.Options().WorldSizeX ||
                    e.getBlock().getZ() > PluginConfig.Options().WorldSizeZ ||
                    e.getBlock().getX() < -PluginConfig.Options().WorldSizeX ||
                    e.getBlock().getZ() < -PluginConfig.Options().WorldSizeX) {
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
            if(e.getDamager().getType() == EntityType.PLAYER)
            {
                Player p = (Player) e.getDamager();

                if(!Utils.IsMember(p, p.getWorld()))
                {
                    e.setCancelled(false);
                }
            }

            if(e.getEntity().getType() == EntityType.PLAYER)
            {
                Player p = (Player) e.getEntity();

                if(!Utils.IsMember(p, p.getWorld()))
                {
                    e.setCancelled(false);
                }
            }
        }
    }
}
