package listeners;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.channel.priv.PrivateChannelCreateEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.RestAction;
import utils.Utils;

import javax.print.DocFlavor;
import java.io.*;
import java.util.List;

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
        // Would be one not zero since person just joined
        if (Utils.getAmountOfPeopleInVoice(event) == 1){
            lookupWatchFileForWatchers(event.getJDA());
        }
    }

    public void lookupWatchFileForWatchers(JDA jda){
        // Lol, does this actually work?
        BufferedReader in = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/watch_list.txt"))
        );
        try{
            String inputLine = "default";
            while ((inputLine = in.readLine()) != null){
                PrivateChannel holding = jda.getUserById(inputLine).openPrivateChannel().complete();
                holding.sendMessage("Someone has joined the voice server. Your name is now removed from watch").queue();
                holding.close().queue();
            }
            wipeWatchFile();
            in.close();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error reading file");
            return;
        }


    }

    public void wipeWatchFile(){
        try{
            PrintWriter printWriter = new PrintWriter(this.getClass().getResource("/watch_list.txt").getPath());
            printWriter.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
            System.out.println("Watch file not found");
        }
    }
}
