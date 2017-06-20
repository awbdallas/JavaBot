package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import utils.Utils;

import java.util.Arrays;

public class MessageCommandParser {
    private String context;

    public MessageCommandParser() {
        this.context = Utils.get_env_var("context_var", true);
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
        String holding = event.getMessage().getStrippedContent();
        String[] split_message;

        if (!holding.substring(0,1).equals(this.context) &&
                holding.length() > 1){
            return null;
        }
        holding = holding.substring(1, holding.length());
        split_message = holding.split(" ");

        if (split_message.length == 1){
            return new ParsedCommandMessage(split_message[0], event);
        }else{
            return new ParsedCommandMessage(split_message[0],
                    Arrays.copyOfRange(split_message, 1, split_message.length),
                    event);
        }
    }
}
