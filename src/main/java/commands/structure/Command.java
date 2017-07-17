package commands.structure;

public interface Command {
    void run(ParsedCommandMessage parsedCommandMessage);
    String getCommand();
}
