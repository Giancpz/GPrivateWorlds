package org.giancpz.gprivateworlds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Queue
{
    public FTPConnection ftp = new FTPConnection();
    public ArrayList<TeleportQueue> teleportQueue = new ArrayList<>();
    public LinkedList<Internal.WorldInfo> loadedworlds = new LinkedList<>();
    private boolean pauseQueue = false;
    public BukkitTask Queuetask;

    private Long period = 10L;

    private void run() {
        Queuetask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.Singleton(), () -> {
            TaskInfo task;

            if(!pauseQueue) task = queue.poll();
            else {
                task = null;
            }

            if (task != null) {
                if (task.taskType == TaskInfo.TaskType.CREATE)
                {
                    if(!Utils.AsyncHasWorld(task.ExecutePlayerName)) {
                        boolean b = Internal.internal_CreatePlayerWorld(task.ExecutePlayerName);
                        if (b) {
                            Messages.Send(Bukkit.getPlayer(task.ExecutePlayerName), Messages.Get().worldCreated, null);
                            //PlayerMessage.Send(task.ExecutePlayerName, "World created!", PlayerMessage.MessageType.INFO ,task.NodeName);
                        }
                    } else {
                        Messages.Send(Bukkit.getPlayer(task.ExecutePlayerName), Messages.Get().worldNotCreated, null);
                        //PlayerMessage.Send(task.ExecutePlayerName, "You have world", PlayerMessage.MessageType.ERROR , task.NodeName);
                    }
                }

                if (task.taskType == TaskInfo.TaskType.TELEPORT || task.taskType == TaskInfo.TaskType.LOAD) {

                    if (Utils.AsyncHasWorld(task.SecondPlayer))
                    {
                        Internal.PlayerInfo p = Utils.AsyncGetPlayerInfoWithName(task.SecondPlayer);
                        Internal.WorldInfo worldInfo = Internal.getWorldInfo(p.WorldUUID);

                        // Not MultiNode Mode
                        if(worldInfo.options.Visibility ||
                                worldInfo.playerOwnerDisplayName.equals(task.ExecutePlayerName) ||
                                Utils.IsMember(Bukkit.getPlayer(task.ExecutePlayerName), worldInfo)) {

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
                                    Player player = Bukkit.getPlayer(task.ExecutePlayerName);
                                    if (player != null && world != null) {

                                        Location loc = world.getSpawnLocation();
                                        Block block = world.getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());

                                        if (block.isEmpty() || block.isLiquid()) {
                                            block.setType(Material.GLASS);
                                        }
                                        player.teleport(world.getSpawnLocation());
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
                            //PlayerMessage.Send(task.ExecutePlayerName, "This world has visits disabled", PlayerMessage.MessageType.ERROR, task.NodeName);
                            Messages.Send(Bukkit.getPlayer(task.ExecutePlayerName),Messages.Get().worldDisabledVisit, null);
                        }
                    }
                    else
                    {
                        if(task.ExecutePlayerName.equals(task.SecondPlayer)) {
                            Messages.Send(Bukkit.getPlayer(task.ExecutePlayerName),Messages.Get().worldNotFoundPlayer, null);
                        } else {
                            String[] args = {task.SecondPlayer};
                            Messages.Send(Bukkit.getPlayer(task.ExecutePlayerName),Messages.Get().worldNotFoundTarget, args);
                        }
                        //PlayerMessage.Send(task.ExecutePlayerName, "You have no world", PlayerMessage.MessageType.ERROR, task.NodeName);
                    }
                }
            }
        }, 120L, period);
    }

    public static class TaskInfo
    {
        public enum TaskType{
            TELEPORT,
            CREATE,
            LOAD,
        }

        public TaskInfo(TaskType tp)
        {
            taskType = tp;
        }

        TaskType taskType;

        public org.bukkit.entity.Player ExecutePlayer;
        public String ExecutePlayerName;
        public String SecondPlayer;
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

    public void Thread()
    {
        period = PluginConfig.Options().queuePeriod;

        if(period < 1)
        {
            Print.warning("The queue period cannot be less than 1, change value and restart the server");
            period = 10L;
        }

         run();
    }

    public void PauseQueue()
    {
        pauseQueue = true;
        Print.warning("QUEUE WAS PAUSED");
    }

    public void ResumeQueue()
    {
        Print.warning("QUEUE WAS RESUMED, GOOD MESSAGE");
        pauseQueue = false;
    }
}
