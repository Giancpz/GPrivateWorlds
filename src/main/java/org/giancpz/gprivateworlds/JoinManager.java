package org.giancpz.gprivateworlds;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.giancpz.gprivateworlds.Internal.*;

public class JoinManager
{
    List<Invitation> invitations = new ArrayList<>();
    private void run()
    {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.Singleton(), () -> {

            Task task = queue.poll();
            if (task != null)
            {
                switch (task.taskType)
                {
                    case INVITE:
                        Invite(task);
                        break;
                    case ACCEPT:
                        Accept(task);
                        break;
                }
            }

        }, 0L, 10L);
    }

    private void Invite(Task task)
    {
        Player player = Bukkit.getPlayer(task.name1);
        PlayerInfo pi1 = Utils.AsyncGetPlayerInfoWithName(task.name1);
        WorldInfo wi1 = getWorldInfo(pi1.WorldUUID);

        // If have world
        if (wi1 != null)
        {
            // If is owner
            if (wi1.playerOwnerUID.equals(pi1.playerUID))
            {
                if (!Utils.AsyncHasWorld(task.name2))
                {
                    SendInvitation(task.name1, task.name2);
                    player.sendMessage("Player invited");
                } else {
                    player.sendMessage("The player you invite already belongs to a world");
                }
            }
            else
            {
                player.sendMessage("You do not own a world");
            }
        }
        else
        {
            player.sendMessage("you don't have a world");
        }
    }

    private void SendInvitation(String name1, String name2)
    {
        invitations.add(new Invitation(name1, name2));
        Player p = Bukkit.getPlayer(name2);
        if (p != null) {
            p.sendMessage(name1 + " Invited you");
        }
    }

    private void Accept(Task task)
    {
        int count = 0;

        List<Invitation> invs = new ArrayList<>();
        for (Invitation invitation : invitations) {
            if(invitation.to.equals(task.name1))
            {
                invs.add(invitation);
                count++;
            }
        }

        if(count == 0 ) {
            Print.debug("No invitations to accept");
        }

        if(count == 1)
        {
            Invitation invitation = invitations.get(0);
            invitations.remove(invitation);
            Print.debug(invitation.from + " to: " + invitation.to);
            AcceptInvitation(invitation.from, invitation.to);
        }
    }

    private void AcceptInvitation(String from, String to)
    {
        Print.debug(from);
        Print.debug(to);

        // If from has world
        if (Utils.AsyncHasWorld(from))
        {
            // If from is owner
            if(Utils.AsyncIsOwner(from))
            {
                // If TO no has world
                //Internal.PlayerInfo toinfo = Internal.getPlayerInfoWithName(to);
                if (!Utils.AsyncHasWorld(to))
                {
                    PlayerInfo FromInfo = Utils.AsyncGetPlayerInfoWithName(from);
                    AdminWorld.AddMember(getWorldInfo(FromInfo.WorldUUID), Utils.AsyncGetPlayerInfoWithName(to));
                    Player player = Bukkit.getPlayer(to);
                    player.sendMessage("Accepted invitation");
                }
            }
        }
        else
        {
            Print.debug("El que te invito no tiene mundo");
        }
    }

    public static void AddTask(Task.taskType taskType, String name1, String name2)
    {
        Task task = new Task();
        task.taskType = taskType;
        task.name1 = name1;
        task.name2 = name2;
        Main.Singleton().joinManager.queue.add(task);
    }

    public static class Task
    {
        public enum taskType
        {
            INVITE,
            ACCEPT
        }

        taskType taskType;

        String name1;
        String name2;
    }

    public static class Invitation
    {
        Invitation(String from, String to){
            this.from = from;
            this.to = to;
        }
        String from;
        String to;
    }

    public BlockingQueue<Task> queue = new ArrayBlockingQueue<>(20);

    public void Thread() {
        run();
    }
}
