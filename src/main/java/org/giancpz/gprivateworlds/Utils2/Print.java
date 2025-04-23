package org.giancpz.gprivateworlds.Utils2;

import org.giancpz.gprivateworlds.Main;

public class Print
{
    public static void info(String message)
    {
        Main.Singleton().getLogger().info(message);
    }

    public static void error(String message)
    {
        Main.Singleton().getLogger().severe(message);
    }

    public static void debug(String message)
    {
        Main.Singleton().getLogger().info("[DEBUG] " + message);
    }
}
