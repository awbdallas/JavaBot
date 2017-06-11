package listeners;

import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 */
public class VoiceChannelListener extends ListenerAdapter{
    VoiceChannelListener () { }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        System.out.printf("[%s][Voice Channel: %s] %s Joined",
                event.getGuild().getName(), event.getChannelJoined().getName(),
                event.getMember().getEffectiveName());
    }
}
