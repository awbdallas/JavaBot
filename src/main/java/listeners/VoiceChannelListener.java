package listeners;

import commands.MessageCommands;
import commands.Watch;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import utils.Utils;

import java.io.*;
import java.util.List;

public class VoiceChannelListener extends ListenerAdapter{
    private MessageCommands messageCommands;

    public VoiceChannelListener(MessageCommands messageCommands) {
        this.messageCommands = messageCommands;
    }

    /**
     * Triggers on guild voice join. Checks to see if anyone needs to be alerted
     * @param event
     */
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        System.out.printf("[%s][Voice Channel: %s] %s Joined",
                event.getGuild().getName(), event.getChannelJoined().getName(),
                event.getMember().getEffectiveName());
        if (Utils.getAmountOfPeopleInVoice(event) == 1){
            for (long user: getWatchObject().getUsersWatchingallList()){
                messageUserAboutAlert(event.getJDA(), user);
            }
        }
        for (long user: getWatchObject().getListOfUsersToMessage(event.getMember().getUser().getIdLong())){
            messageUserAboutAlert(event.getJDA(), user);
        }
    }

    public void messageUserAboutAlert(JDA jda, Long id){
        PrivateChannel holding = jda.getUserById(id).openPrivateChannel().complete();
        holding.sendMessage("User you were watching has joined the server").queue();
        holding.close().queue();
    }

    public Watch getWatchObject() {
        for (Object object : messageCommands.getCommandObjects()){
            if (object instanceof Watch){
                return (Watch) object;
            }
        }
        return null;
    }
}
