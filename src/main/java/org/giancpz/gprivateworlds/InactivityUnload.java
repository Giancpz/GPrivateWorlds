package org.giancpz.gprivateworlds;

import org.bukkit.Bukkit;
import java.time.Duration;
import java.time.Instant;

public class InactivityUnload
{
    public void Start()
    {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.Singleton(), () ->
        {
            if(PluginConfig.Options().inactivity > 0)
            {
                Internal.WorldInfo select = null;
                for (Internal.WorldInfo w : Main.Singleton().queue.loadedworlds) {
                    Duration duration = Duration.between(w.lastPlayer, Instant.now());
                    if (duration.getSeconds() > PluginConfig.Options().inactivity) {
                        if (w.world.getPlayers().isEmpty()) {
                            select = w;
                            Internal.SaveWorld(w);
                        } else {
                            w.lastPlayer = Instant.now();
                            Print.debug("World with players " + w.world.getName());
                        }
                        break;
                    }
                }
                if (select != null) Main.Singleton().queue.loadedworlds.remove(select);
            }
        }, 120L, 200L);
    }
}
