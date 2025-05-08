package org.giancpz.gprivateworlds;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

import static org.giancpz.gprivateworlds.Internal.*;

public class JoinManager
{
    List<Invitation> invitations = new ArrayList<>();

    public void Invite(Player execute, String invite)
    {
        for (Invitation invitation : invitations)
        {
            if(invitation.from.equals(execute.getName()) && invitation.to.equals(invite)) return;
            else PlayerMessage.Send(execute, "You already invited this player", PlayerMessage.MessageType.ERROR);
        }

        PlayerInfo pi1 = Utils.AsyncGetPlayerInfoWithName(execute.getName());
        WorldInfo wi1 = getWorldInfo(pi1.WorldUUID);

        // If have world
        if (wi1 != null)
        {
            // If is owner
            if (wi1.playerOwnerUID.equals(pi1.playerUID))
            {
                if (!Utils.AsyncHasWorld(invite))
                {
                    SendInvitation(execute.getName(), invite);
                    PlayerMessage.Send(execute, "Player invited", PlayerMessage.MessageType.INFO);
                } else {
                    PlayerMessage.Send(execute, "The player you invite already belongs to a world", PlayerMessage.MessageType.ERROR);
                }
            }
            else
            {
                PlayerMessage.Send(execute, "You do not own a world", PlayerMessage.MessageType.ERROR);
            }
        }
        else
        {
            PlayerMessage.Send(execute, "you don't have a world", PlayerMessage.MessageType.ERROR);
        }
    }

    private void SendInvitation(String name1, String name2)
    {
        invitations.add(new Invitation(name1, name2));
        Player p = Bukkit.getPlayer(name2);
        if (p != null) {
            PlayerMessage.Send(p, name1 + " Invited you", PlayerMessage.MessageType.INFO);
        }
    }

    public void Accept(Player player, String accept)
    {
        int count = 0;

        List<Invitation> invs = new ArrayList<>();
        for (Invitation invitation : invitations) {
            if(invitation.to.equals(player.getName()))
            {
                invs.add(invitation);
                count++;
            }
        }

        if(count == 0) {
            Print.debug("No invitations to accept");
        }

        if(count == 1)
        {
            Invitation invitation = invitations.get(0);
            invitations.remove(invitation);
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
                if (!Utils.AsyncHasWorld(to))
                {
                    PlayerInfo FromInfo = Utils.AsyncGetPlayerInfoWithName(from);
                    AdminWorld.AddMember(getWorldInfo(FromInfo.WorldUUID), Utils.AsyncGetPlayerInfoWithName(to));
                    Player player = Bukkit.getPlayer(to);
                    PlayerMessage.Send(player, "Invitation accepted! use /pw home", PlayerMessage.MessageType.INFO);
                }
            }
        }
        else
        {
            Print.debug("Error 3");
        }
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
}
