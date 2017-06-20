package listeners;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import utils.Utils;

import java.io.*;

/**
 *
 */
public class VoiceChannelListener extends ListenerAdapter{

    public VoiceChannelListener() { }

    /**
     * Triggers on guild voice join. At the moment it just watches and checks
     * the watch file to figure out if it needs to ping anyone.
     * @param event
     */
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        System.out.printf("[%s][Voice Channel: %s] %s Joined",
                event.getGuild().getName(), event.getChannelJoined().getName(),
                event.getMember().getEffectiveName());
        if (Utils.getAmountOfPeopleInVoice(event) == 1){
            lookupWatchFileForWatchers(event.getJDA());
        }
    }

    public void lookupWatchFileForWatchers(JDA jda){
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
