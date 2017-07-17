package commands.structure;

import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageCommands {
    private List<Command> commandObjects;


    public MessageCommands () {
        Reflections reflections = new Reflections("commands");
        Set<Class<? extends Command>> subtypes = reflections.getSubTypesOf(Command.class);

        commandObjects = new ArrayList<>(subtypes.size());

        for (Class command : subtypes) {
            try{
                commandObjects.add(Command.class.cast(command.newInstance()));
            }catch (InstantiationException e){
                e.printStackTrace();
            }catch (IllegalAccessException e){
               e.printStackTrace();
            }
        }
    }

    public void runCommand(ParsedCommandMessage parsedCommandMessage){
        for (Command command : commandObjects) {
            if (command.getCommand().equals(parsedCommandMessage.getCommand())){
               command.run(parsedCommandMessage);
               return;
            }
        }
        parsedCommandMessage.setResponse("Unable to find command");
    }

    public List<Command> getCommandObjects() {
        return commandObjects;
    }
}
