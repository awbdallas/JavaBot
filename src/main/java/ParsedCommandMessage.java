public class ParsedCommandMessage {
    private String command;
    private String[] arguments;

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

    public void setCommand(String newCommand){ this.command = newCommand; }
    public void setArguments(String[] arguments) { this.arguments = arguments; }

    public String[] getArguments(){ return this.arguments; }
    public String getCommand(){ return this.command; }
}
