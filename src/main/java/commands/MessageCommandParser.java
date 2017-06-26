package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import utils.Utils;

import java.util.Arrays;

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
                    Arrays.copyOfRange(splitMessage, 1, splitMessage.length),
                    event);
        }
    }
}
