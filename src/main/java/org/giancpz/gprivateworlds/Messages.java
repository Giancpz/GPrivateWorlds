package org.giancpz.gprivateworlds;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Properties;

public class Messages
{
    /*
    public String worldNotFoundPlayer = "worldNotFoundPlayer=No perteneces a un mundo";
    public String worldNotFoundTarget = "worldNotFoundTarget={0} no pertenece a un mundo";
    public String worldNotOwn = "worldNotOwn=&cNo eres dueño de este mundo";
    public String worldNotLeave = "worldNotLeave=No puedes salir del mundo si eres el dueño";
    public String worldNotJoin = "worldNotJoin=&cNo pudiste unirte al mundo";
    public String worldSetSpawn = "worldSetSpawn=&aCambiaste el spawn";
    public String worldMemberDelete = "worldMemberDelete=&a{0} ya no pertenece a este mundo";
    public String worldNotCreated = "worldNotCreated=Ya perteneces a un mundo @ENTITY_GENERIC_EAT 1 1.3";
    public String worldCreated = "worldCreated=&aMundo creado! Usa &b/pw home &apara ir @ENTITY_EXPERIENCE_ORB_PICKUP 1 1";
    public String worldDeleteWarning = "worldDeleteWarning=&cTu mundo se eliminara para siempre, usa &e/pw delete confirm";
    public String worldJoin = "worldJoin=Te uniste al mundo de {0} @ENTITY_PLAYER_LEVELUP 1 1.5";
    public String worldLeave = "worldLeave=&aSaliste del mundo";
    public String worldDisabledVisit = "worldDisabledVisit=&cEste mundo tiene las visitas desactivadas";
    public String invitationNot = "invitationNot=No tienes invitaciones";
    public String invitationSent = "invitationSent=Invitaste a {0} a unirse a tu mundo @ENTITY_GENERIC_EAT 1 1.3";
    public String invitationReceived = "invitationReceived={0} te invitó a unirse a su mundo @ENTITY_PLAYER_LEVELUP 1 1.5";
    public String invitationNotSent = "invitationNotSent=Ya invitaste a {0}";
    public String invitationNotSent1 = "invitationNotSent1=No puedes invitar a{0} porque ya pertenece a un mundo";
    */


    public String worldNotFoundPlayer = "worldNotFoundPlayer=You do not belong to a world";
    public String worldNotFoundTarget = "worldNotFoundTarget={0} does not belong to a world";
    public String worldNotOwn = "worldNotOwn=&cYou are not the owner of this world";
    public String worldNotLeave = "worldNotLeave=You cannot leave the world if you are the owner";
    public String worldNotJoin = "worldNotJoin=&cYou could not join the world";
    public String worldSetSpawn = "worldSetSpawn=&aSpawn point changed";
    public String worldMemberDelete = "worldMemberDelete=&a{0} no longer belongs to this world";
    public String worldNotCreated = "worldNotCreated=You already belong to a world @ENTITY_GENERIC_EAT 1 1.3";
    public String worldCreated = "worldCreated=&aWorld created! Use &b/pw home &a to go @ENTITY_EXPERIENCE_ORB_PICKUP 1 1";
    public String worldDeleteWarning = "worldDeleteWarning=&cYour world will be permanently deleted, use &e/pw delete confirm";
    public String worldJoinPlayer = "worldJoinPlayer=You joined {0}'s world @ENTITY_PLAYER_LEVELUP 1 1.5";
    public String worldLeavePlayer = "worldLeavePlayer=&aYou left the world";
    public String worldDisabledVisit = "worldDisabledVisit=&cThis world has disabled visits";
    public String invitationNot = "invitationNot=You have no invitations";
    public String invitationSent = "invitationSent=You invited {0} to join your world @ENTITY_GENERIC_EAT 1 1.3";
    public String invitationReceived = "invitationReceived={0} invited you to join their world @ENTITY_PLAYER_LEVELUP 1 1.5";
    public String invitationNotSent = "invitationNotSent=You have already invited {0}";
    public String invitationNotSent1 = "invitationNotSent1=You cannot invite {0} because they already belong to a world";

    public int changes = 0;

    public String VerifyValue(Properties properties, String message)
    {
        String[] parts = message.split("=");

        if(!properties.containsKey(parts[0]))
        {
            Print.warning(parts[0] + " does not exist, it has been added to messages.properties. The default value will be used");
            properties.setProperty(parts[0], parts[1]);
            changes++;
        }
        return properties.getProperty(parts[0]);
    }
    public void Init() {
        Properties prop = new Properties();
        try {
            String path = "plugins/GPrivateWorlds/messages.properties";
            File file = new File(path);

            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Print.error("Error to load messages");
                }
            }
            prop.load(Files.newInputStream(Paths.get(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        worldNotFoundPlayer = VerifyValue(prop, worldNotFoundPlayer);
        worldNotFoundTarget = VerifyValue(prop, worldNotFoundTarget);
        worldNotOwn = VerifyValue(prop, worldNotOwn);
        worldNotLeave = VerifyValue(prop, worldNotLeave);
        worldNotJoin = VerifyValue(prop, worldNotJoin);
        worldSetSpawn = VerifyValue(prop, worldSetSpawn);
        worldMemberDelete = VerifyValue(prop, worldMemberDelete);
        worldNotCreated = VerifyValue(prop, worldNotCreated);
        worldDeleteWarning = VerifyValue(prop, worldDeleteWarning);
        invitationNotSent1 = VerifyValue(prop, invitationNotSent1);
        worldCreated = VerifyValue(prop, worldCreated);
        worldJoinPlayer = VerifyValue(prop, worldJoinPlayer);
        worldLeavePlayer = VerifyValue(prop, worldLeavePlayer);
        worldDisabledVisit = VerifyValue(prop, worldDisabledVisit);
        invitationNot = VerifyValue(prop, invitationNot);
        invitationSent = VerifyValue(prop, invitationSent);
        invitationReceived = VerifyValue(prop, invitationReceived);
        invitationNotSent = VerifyValue(prop, invitationNotSent);

        if (changes > 0)
        {
            try (FileOutputStream output = new FileOutputStream("plugins/GPrivateWorlds/messages.properties")) {
                prop.store(output, changes + " properties were added ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void Send(Player player, String message, String[] arguments)
    {
        Main.Singleton().messages.internal_send(player, message, arguments);
    }

    public void internal_send(Player player, String message, String[] arguments) {
        String[] parts = message.split("@");
        if (parts.length > 1) {
            String sound = parts[1];
            try
            {
                String[] p = sound.split(" ");
                float volume = 1f;
                float pitch = 1f;

                if(parts.length > 2) volume = Float.parseFloat(p[0]);
                if(parts.length > 3) pitch = Float.parseFloat(p[1]);

                Sound s = Sound.valueOf(p[0]);
                player.playSound(player.getLocation(), s, volume, pitch);
            } catch (IllegalArgumentException e)
            {
                Print.error(sound + " is not a valid sound");
            }
        }
        String msg;
        if (arguments != null) {
            msg = MessageFormat.format(parts[0], (Object[]) arguments);
        }
        else msg = parts[0];

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static Messages Get()
    {
        return Main.Singleton().messages;
    }
}
