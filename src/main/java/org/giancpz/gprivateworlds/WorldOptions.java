package org.giancpz.gprivateworlds;

import org.bukkit.GameRule;
import org.bukkit.World;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldOptions
{
    public static class Options
    {
        public boolean Visibility = true;
        public boolean pvp = false;

        List<String> Options = new ArrayList();

        public void Set()
        {
            for (String v : Options)
            {
                String[] dat = v.split(":");
                if (dat.length >= 2)
                {
                    String key = dat[0];
                    String value = dat[1];
                    switch (key)
                    {
                        case "visibility":
                            Visibility = Boolean.parseBoolean(value);
                            break;
                        case "pvp":
                            pvp = Boolean.parseBoolean(value);
                            break;
                    }
                }
            }
        }

        public void SetDefult()
        {
            Options =  PluginConfig.Options().defaultWorldOptions;
        }

        public void GetValuesFromString(String options)
        {
            String[] values = options.split(",");
            Options = Arrays.asList(values);
        }

        public String ToText()
        {
            String str = "pvp " + pvp + ",visibility " + Visibility;
            Print.debug(str);
            return str;
        }
    }

    public static class Gamerules {
        List<String> gamerules;

        Gamerules()
        {
            gamerules =  PluginConfig.Options().gamerules;
        }

        public void SetToWorld(World world)
        {
            for (String gamerule : gamerules) {
                String[] s = gamerule.split(" ");
                GameRule gameRule = GameRule.getByName(s[0]);

                if (gameRule != null) {
                    // Modificar el GameRule
                    world.setGameRule(gameRule, s[1]);
                }
            }
        }
    }

    public static class CreationDate {
        public int month;
        public int day;
        public int year;

        public String toText()
        {
            return String.format("%d;%d;%d", month, day, year);
        }

        public void toObject(String text)
        {
            String[] as = text.split(";");
            month = Integer.parseInt(as[0]);
            day = Integer.parseInt(as[1]);
            year = Integer.parseInt(as[2]);
        }
    }

    public static class SpawnLocation {
        public int x;
        public int y;
        public int z;

        public void set(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public String toText()
        {
            return String.format("%d;%d;%d", x, y, z);
        }

        public void toObject(String text)
        {
            String[] as = text.split(";");
            x = Integer.parseInt(as[0]);
            y = Integer.parseInt(as[1]);
            z = Integer.parseInt(as[2]);
        }
    }
}
