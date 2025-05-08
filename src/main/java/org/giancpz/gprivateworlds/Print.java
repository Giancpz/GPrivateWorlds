package org.giancpz.gprivateworlds;

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

    public static void warning(String message)
    {
        Main.Singleton().getLogger().warning(message);
    }

    public static void debug(String message)
    {
        if(PluginConfig.Options().debug){
            Main.Singleton().getLogger().info("[DEBUG] " + message);
        }
    }
}
