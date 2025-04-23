package org.giancpz.gprivateworlds;

import org.bukkit.GameRule;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class WorldOptions
{
    public static class Gamerules
    {
        List<String> gamerules = new ArrayList<String>();

        Gamerules()
        {
            gamerules.add("doDaylightCycle false");
            gamerules.add("pvp false");
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

        /*
        public boolean Value(String key) {
                for (String par : flags) {
                    String[] claveValor = par.split(" ");
                    if (claveValor[0].equals(key)) {
                        return Boolean.parseBoolean(claveValor[1]);
                    }
                }
            return false;
        }
        */
    }

    public static class CreationDate
    {
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

    public static class SpawnLocation
    {
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
