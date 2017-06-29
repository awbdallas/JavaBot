package commands;

public interface Command {
    void run(ParsedCommandMessage parsedCommandMessage);
    String getCommand();
}
