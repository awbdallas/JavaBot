import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ParsedCommandMessage {
    private String command;
    private String[] arguments;
    private MessageReceivedEvent event;

    ParsedCommandMessage(String command){
        this.command = command;
        // Trying to avoid null problems, but still making sure
        // No arguments
        this.arguments = new String[]{"None"};
    }

    ParsedCommandMessage(String command, String[] arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    ParsedCommandMessage(String command, MessageReceivedEvent event) {
        this.command = command;
        this.arguments = arguments;
        this.event = event;
    }

    public void setCommand(String newCommand){ this.command = newCommand; }
    public void setArguments(String[] arguments) { this.arguments = arguments; }
    public void setEvent(MessageReceivedEvent event) { this.event = event; }

    public String[] getArguments(){ return this.arguments; }
    public String getCommand(){ return this.command; }
    public MessageReceivedEvent getEvent(){ return this.event; }
}
