/**
 * Created by awbriggs on 5/1/17.
 */
public class ParsedMessage {
    private String command;
    private String[] arguments;

    ParsedMessage(String command){
        this.command = command;
        // Trying to avoid null problems, but still making sure
        // No arguments
        this.arguments = new String[]{"None"};
    }

    ParsedMessage(String command, String[] arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public void setCommand(String newCommand){ this.command = newCommand; }
    public void setArguments(String[] arguments) { this.arguments = arguments; }

    public String[] getArguments(){ return this.arguments; }
    public String getCommand(){ return this.command; }
}
