package org.giancpz.gprivateworlds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.giancpz.gprivateworlds.Utils2.Print;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Queue
{
    public FTPConnection ftp = new FTPConnection();
    public ArrayList<TeleportQueue> teleportQueue = new ArrayList<TeleportQueue>();
    public ArrayList<Internal.WorldInfo> loadedworlds = new ArrayList<>();

    private void run() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.Singleton(), () -> {
            TaskInfo task = queue.poll();
            if (task != null) {
                if (task.taskType == TaskInfo.TaskType.CREATE)
                {
                    if(!Utils.AsyncHasWorld(task.ExecutePlayerName)) {
                        boolean b = Internal.internal_CreatePlayerWorld(task.ExecutePlayerName);
                        if (b) {
                            PlayerMessage.Send(task.ExecutePlayerName, "World created!", task.NodeName);
                        }
                    } else {
                        PlayerMessage.Send(task.ExecutePlayerName, "You have world", task.NodeName);
                    }
                }

                if (task.taskType == TaskInfo.TaskType.TELEPORT || task.taskType == TaskInfo.TaskType.LOAD) {
                    Internal.PlayerInfo p = Utils.AsyncGetPlayerInfoWithName(task.playerWorld);

                    if (Utils.AsyncHasWorld(p))
                    {

                        Internal.WorldInfo worldInfo = Internal.getWorldInfo(p.WorldUUID);

                        Bukkit.getScheduler().runTask(Main.Singleton(), () -> {

                            org.bukkit.World world;

                            if (Main.Singleton().queue.loadedworlds.contains(worldInfo)) {
                                world = worldInfo.world;
                                Print.debug("World is loaded");
                            } else {
                                world = Internal.internal_LoadPlayerWorld(worldInfo);
                                loadedworlds.add(worldInfo);
                                Print.debug("Loading world");
                            }

                            if (task.taskType.equals(TaskInfo.TaskType.TELEPORT)) {
                                Location location = new Location(world, 0, 100, 0);
                                Player player = Bukkit.getPlayer(task.ExecutePlayerName);
                                if (player != null) {
                                    player.teleport(location);
                                }
                            }

                            if (task.otherparam != null) {
                                if (task.otherparam.equals("notifyload")) {
                                    teleportQueue.add(new TeleportQueue(task.ExecutePlayerName, world));
                                    Main.Singleton().node.Send("central", "bungee-teleport:" + p.playerName);
                                }
                            }
                        });
                    }
                    else
                    {
                        PlayerMessage.Send(task.ExecutePlayerName, "You have no world", task.NodeName);
                    }
                }
            }
        }, 0L, 10L);
    }

    public static class TaskInfo
    {
        enum TaskType{
            TELEPORT,
            CREATE,
            LOAD
        }

        TaskInfo(TaskType tp)
        {
            taskType = tp;
        }

        TaskType taskType;

        public org.bukkit.entity.Player ExecutePlayer;
        public String ExecutePlayerName;
        public String playerWorld;
        public String NodeName;
        public String otherparam;
    }

    public static class TeleportQueue
    {
        String PlayerName;
        World toWorld;

        public TeleportQueue(String playerName, World toWorld)
        {
            this.PlayerName = playerName;
            this.toWorld = toWorld;
        }
    }

    public BlockingQueue<TaskInfo> queue = new ArrayBlockingQueue<>(20);

     public void Thread() {
         run();
    }
}
