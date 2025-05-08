package org.giancpz.gprivateworlds;

import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker
{
    public static void Check()
    {
        new BukkitRunnable() {
            @Override
            public void run()
            {
                try
                {
                    String versionURL = "https://raw.githubusercontent.com/Giancpz/GPrivateWorlds/refs/heads/master/lastversion";

                        URL url = new URL(versionURL);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");

                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String content = reader.readLine();
                        reader.close();

                        if (!content.equals(Main.Singleton().getDescription().getVersion())) {
                            Print.warning("**********************************************");
                            Print.warning("New version of GPrivateWorlds is available! ");
                            Print.warning("Download in: https://www.spigotmc.org/resources/gprivateworlds-private-worlds.124714/");
                            Print.warning("**********************************************");
                        }
                    } catch (Exception e) {
                        Print.error("Error to check update: " + e.getMessage());
                    }
                }
        }.runTaskAsynchronously(Main.Singleton());
    }
}
