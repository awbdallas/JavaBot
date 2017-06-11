package commands;

import commands.ChannelSorter;
import commands.Ping;
import commands.Xkcd;

public class MessageCommands {
    /**
     * This was created to deal with actually running the commands.
     * At the moment it's not that sophisticated and just runs via
     * a switch. May make changes in the future.
     *
     * @param command which is a parsed message containing commands
     *                and arguments
     * @return String with what should be the response
     */
    public static String runCommand(ParsedCommandMessage command){
        String response;
        switch(command.getCommand()){
            case "ping":
                response = Ping.run();
                break;
            case "xkcd":
                Xkcd xkcd = new Xkcd();
                response = xkcd.run(command);
                break;
            case "sortchannels":
                response = ChannelSorter.run(command.getEvent().getGuild());
                break;
            default:
                response = "Command not found";
        }
        return response;
    }
}
