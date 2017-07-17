package commands.structure;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class ParsedCommandMessage {
    private String command;
    private List<String[]> arguments;
    private MessageReceivedEvent event;
    private String response = "";

    ParsedCommandMessage(String command, MessageReceivedEvent event){
        this.command = command;
        this.arguments = new ArrayList<>();
        this.event = event;
    }

    ParsedCommandMessage(String command, List<String[]> arguments,
                         MessageReceivedEvent event) {
        this.command = command;
        this.arguments = arguments;
        this.event = event;
    }

    public String argumentsToString(){
        String returning = "";

        for (String[] holding : this.arguments){
           returning += holding.toString() + " ";
        }
        return returning;
    }

    public void appendToResponse(String toAppend){
        if (toAppend != null){
            this.response = this.response + " " + toAppend;
        }
    }

    public void setCommand(String newCommand) { this.command = newCommand; }
    public void setArguments(List<String[]> arguments) { this.arguments = arguments; }
    public void setEvent(MessageReceivedEvent event) { this.event = event; }
    public void setResponse(String response) { this.response = response; }

    public List<String[]> getArguments() { return this.arguments; }
    public String getCommand() { return this.command; }
    public MessageReceivedEvent getEvent() { return this.event; }
    public String getResponse() { return this.response; }
}
