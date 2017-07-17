package utils;

import commands.structure.ParsedCommandMessage;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Utils {
    public final static String LINK_REGEX = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})";
    public final static String COMMAND_REGEX = ":(.*?):";


    /**
     * Returns requested env var. Will fail if you want it too
     * @param envvar (variable you want) fail (want it to fail?)
     * @return  string token from env.
     */
    public static String getEnvVar(String envvar, boolean fail){
        String var = System.getenv(envvar);
        if (var == null){
            System.err.println(String.format("Environment variable: (%s) not set", envvar));
            if (fail){
                System.exit(1);
            }
        }
        return var;
    }

    /**
     * Needed a way to say the difference between times
     * @param first, second which are both Messages.
     * @return  string with the time and type of the time since between the first and second messages
     */
    public static String getMessageTimeDifference(Message first, Message second){
        long secondsDifference = second.getCreationTime().toEpochSecond() - first.getCreationTime().toEpochSecond();

        if(secondsDifference / 86400 >= 1){
            return String.format("%d days", secondsDifference / 86400);
        } else if(secondsDifference / 3600 >= 1){
            return String.format("%d hours", secondsDifference / 3600);
        } else if(secondsDifference / 60 >= 1){
            return String.format("%d minutes", secondsDifference / 60);
        }else{
            return String.format("%d seconds", secondsDifference);
        }
    }

    /**
     * Log command is just to output. May be changed later to actually have a log, but this
     * would be easier to change in the future
     * @param   parsed_command which is the package now for all the commands
     */
    public static void logCommand(ParsedCommandMessage parsed_command){
        MessageReceivedEvent event = parsed_command.getEvent();
        System.out.printf("[%s][%s] Command: %s Arguments: %s\n", event.getGuild(), event.getTextChannel(),
                parsed_command.getCommand(), parsed_command.argumentsToString());
    }

    /**
     * Log command is just to output. May be changed later to actually have a log, but this
     * would be easier to change in the future. This one also gives out result
     * @param   parsed_command, result
     * @returns none
     */
    public static void logCommand(ParsedCommandMessage parsed_command, String result){
        MessageReceivedEvent event = parsed_command.getEvent();
        System.out.printf("[%s][%s] Result: %s\n", event.getGuild().getName(),
                event.getTextChannel().getName(), result);
    }

    /**
     * Way to output easily based on text rather than a command
     * @param   event, found, result
     * @returns none
     */
    public static void logText(MessageReceivedEvent event, String found, String result){
        System.out.printf("[%s][%s] Found: %s Result: %s\n", event.getGuild().getName(), event.getTextChannel().getName(),
                found, result);
    }

    /**
     *
     * @param event GuildVoiceJoinEvent for grabbing guild and voice channels
     * @return count of amount of people that are in that guilds voice server
     */
    public static int getAmountOfPeopleInVoice(GuildVoiceJoinEvent event) {
        int count = 0;
        for (VoiceChannel voiceChannel : event.getGuild().getVoiceChannels()){
            if (!(voiceChannel == event.getGuild().getAfkChannel())){
                count += voiceChannel.getMembers().size();
            }
        }
        return count;
    }
}
