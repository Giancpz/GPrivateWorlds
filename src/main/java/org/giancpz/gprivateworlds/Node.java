package org.giancpz.gprivateworlds;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.giancpz.gprivateworlds.PluginConfig.Nodemode;

import java.util.ArrayList;
import java.util.UUID;

public class Node
{
    IMessage msg;
    Nodemode nodemode;
    String nodename = "mini-a56";

    ArrayList<ClientNode> Clients = new ArrayList<>();

    public void InitNode()
    {
        nodemode = PluginConfig.Options().nodemode;

        if(nodemode == Nodemode.MAIN) {
            nodename = "main";
        }

        if(nodemode != Nodemode.LOCAL)
        {
            Main.Singleton().getLogger().info("Load in MultiNode: " + nodemode);
            msg = new RabbitMQ();
            msg.init(this);
        }
    }

    public void CreateWorld(String playername)
    {
        if(nodemode == Nodemode.CLIENT)
        {
            Send("central", "createworld:" + playername);
        }

        if(nodemode == Nodemode.LOCAL || nodemode == Nodemode.MAIN)
        {
            Queue.TaskInfo task = new Queue.TaskInfo(Queue.TaskInfo.TaskType.CREATE);
            task.ExecutePlayerName = playername;
            try {
                Main.Singleton().queue.queue.put(task);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void TeleportToWorld(String playername, String toWorld)
    {
        switch(nodemode){
            case LOCAL:
                Internal.AddManagerTask(null, playername, toWorld, Queue.TaskInfo.TaskType.TELEPORT,null);
            break;
            case MAIN:
                for (ClientNode c : Clients) {
                    Main.Singleton().node.Send(c.nodeID,"teleport:"+ playername + ":" + toWorld);
                    break;
                }
            break;
        }
    }

    public void Send(String to,String message)
    {
        String s = nodename + "=" + message;
        msg.Send(to, s);
    }

    public void Received(String msg)
    {
        String[] dat = msg.split("=");
        String nodeid = dat[0];
        String[] params = dat[1].split(":");
        if(params[0].equals("teleport"))
        {
            Internal.AddManagerTask(null, params[1], params[2], Queue.TaskInfo.TaskType.LOAD, "notifyload");
        }

        if(params[0].equals("bungee-teleport"))
        {
            Player player = Bukkit.getPlayer(params[1]);
            if (player != null) {
                player.sendMessage("Sending to mini-a56");
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("mini-a56");
                player.sendPluginMessage(Main.Singleton(), "BungeeCord", out.toByteArray());
            }
        }

        if(params[0].equals("request"))
        {
            Main.Singleton().getLogger().info("MultiNode: Client " + nodeid + " connected");
            ClientNode cn = new ClientNode(true, nodeid);
            Clients.add(cn);
            Send(nodeid, "accept");
        }

        if(params[0].equals("loaded"))
        {

        }

        if(params[0].equals("createworld"))
        {
            Queue.TaskInfo task = new Queue.TaskInfo(Queue.TaskInfo.TaskType.CREATE);
            task.ExecutePlayerName = params[1];
            task.otherparam = "notify-player";
            task.NodeName = nodeid;

            try {
                Main.Singleton().queue.queue.put(task);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if(params[0].equals("notify-player"))
        {
            Player player = Bukkit.getPlayer(params[1]);
            if (player != null) {
                player.sendMessage(params[2]);
            }
        }
    }

    public static class ClientNode
    {
        boolean connected;
        String nodeID;
        ArrayList<String> loadedworld = new ArrayList<>();

        public ClientNode(boolean connected,String nodeID)
        {
            this.connected = connected;
            this.nodeID = nodeID;
        }
    }
}
