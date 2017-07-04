package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageCommandParser {
    private String context;

    public MessageCommandParser() {
        this.context = Utils.getEnvVar("context_var", true);
    }

    public MessageCommandParser(String context){
        this.context = context;
    }

    /** Checking if it's a command, then grabbing command and
     * any arguments it may have.
     * @param  event which is a typeof event
     * @returns array with 0 being command and 1 - end being arguments
     */
    public ParsedCommandMessage parseMessage(MessageReceivedEvent event){
        String strippedMessage = event.getMessage().getStrippedContent();
        String[] splitMessage;

        if (!strippedMessage.substring(0, 1).equals(this.context) || strippedMessage.length() == 1){
            return null;
        }

        strippedMessage = strippedMessage.substring(1, strippedMessage.length());
        splitMessage = strippedMessage.split(" ");

        if (splitMessage.length == 1){
            return new ParsedCommandMessage(splitMessage[0], event);
        }else{
            return new ParsedCommandMessage(splitMessage[0],
                    parseArguments(Arrays.copyOfRange(splitMessage, 1, splitMessage.length)), event);
        }
    }

    /**
     * Breaking up into command + arguments
     * @param arguments
     * @return
     */
    public List<String[]> parseArguments(String[] arguments) {
        List<String[]> listOfCommands = new ArrayList<>();
        List<String> buildingListOfArguments = new ArrayList<>();

        for (int i = 0; i < arguments.length; i ++) {
            if (arguments[i].substring(0,1).equals("-")) {
                if (buildingListOfArguments.size() != 0){
                    listOfCommands.add(buildingListOfArguments.toArray(new String[buildingListOfArguments.size()]));
                    buildingListOfArguments.clear();
                }
                buildingListOfArguments.add(arguments[i].substring(1));
            }else{
                buildingListOfArguments.add(arguments[i]);
            }
        }
        if (buildingListOfArguments.size() != 0){
            listOfCommands.add(buildingListOfArguments.toArray(new String[buildingListOfArguments.size()]));
        }
        return listOfCommands;
    }
}
