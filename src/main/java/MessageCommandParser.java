import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class MessageCommandParser {
    private String context;

    MessageCommandParser() {
        this.context = getContextVariable();
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

    /** Grabbing context variable setting from environement
     * @returns String context (environemnt variable)
     */
    private static String getContextVariable(){
        String context = System.getenv("context_var");
        if (context == null){
            System.err.println("Context variable not set:");
            System.exit(1);
        }else if (context.length() != 1){
            System.err.println("Invalid length for context");
            System.exit(1);
        }
        return context;
    }
}
