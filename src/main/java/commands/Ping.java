package commands;

public class Ping implements Command {
    private final String command = "ping";
    /**
     * Response with a pong to pings
     * @return string with pong
     */
     public void run(ParsedCommandMessage parsedCommandMessage) {
         parsedCommandMessage.setResponse("pong");
    }

    public String getCommand() {
         return command;
    }
}
