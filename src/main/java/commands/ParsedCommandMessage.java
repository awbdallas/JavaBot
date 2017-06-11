package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ParsedCommandMessage {
    private String command;
    private String[] arguments;
    private MessageReceivedEvent event;

    ParsedCommandMessage(String command, MessageReceivedEvent event){
        this.command = command;
        // Trying to avoid null problems, but still making sure
        // No arguments
        this.arguments = new String[]{"None"};
        this.event = event;
    }

    ParsedCommandMessage(String command, String[] arguments,
                         MessageReceivedEvent event) {
        this.command = command;
        this.arguments = arguments;
        this.event = event;
    }

    public String arguments_to_string(){
        String returning = "";

        for (String holding : this.arguments){
           returning = returning + holding + " ";
        }
        return returning;
    }

    public void setCommand(String newCommand){ this.command = newCommand; }
    public void setArguments(String[] arguments) { this.arguments = arguments; }
    public void setEvent(MessageReceivedEvent event) { this.event = event; }

    public String[] getArguments(){ return this.arguments; }
    public String getCommand(){ return this.command; }
    public MessageReceivedEvent getEvent(){ return this.event; }

}
