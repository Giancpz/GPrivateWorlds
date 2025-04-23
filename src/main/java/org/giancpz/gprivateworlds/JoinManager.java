package org.giancpz.gprivateworlds;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.giancpz.gprivateworlds.Utils2.Print;
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
        PlayerInfo pi1 = Utils.AsyncGetPlayerInfoWithName(task.name1);
        WorldInfo wi1 = getWorldInfo(pi1.WorldUUID);

        // If have world
        if (wi1 != null)
        {
            // If is owner
            if (wi1.playerOwnerUID.equals(pi1.playerUID))
            {
                PlayerInfo pi2 = Utils.AsyncGetPlayerInfoWithName(task.name2);
                if (pi2 == null)
                {
                    SendInvitation(task.name1, task.name2);
                    Print.debug("Jugador invitado");
                } else {
                    Print.debug("El jugador ya tiene mundo");
                }
            }
            else
            {
                Print.debug("No eres dueño");
            }
        }
        else
        {
            Print.debug("No tienes mundo");
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
            Print.debug("No tienes invitaciones :(");
        }

        if(count == 1)
        {
            Invitation invitation = invitations.get(0);
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
            if(Utils.IsOwner(from))
            {
                // If TO no has world
                //Internal.PlayerInfo toinfo = Internal.getPlayerInfoWithName(to);
                if (Utils.AsyncHasWorld(to))
                {
                    PlayerInfo FromInfo = Utils.AsyncGetPlayerInfoWithName(from);
                    AdminWorld.AddMember(getWorldInfo(FromInfo.WorldUUID), Utils.AsyncGetPlayerInfoWithName(to));
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
