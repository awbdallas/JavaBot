package commands.ping;

import commands.structure.Command;
import commands.structure.ParsedCommandMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ping implements Command {
    private final String command = "ping";
    private Map<String, Integer> mapOfUsersToUsersCalled;

    public Ping() {
        mapOfUsersToUsersCalled = new HashMap<>();
    }

    /**
     * Response with a pong to pings
     * @return string with pong
     */
     public void run(ParsedCommandMessage parsedCommandMessage) {
         if (parsedCommandMessage.getArguments().size() == 0){
             runWithoutArguments(parsedCommandMessage);
         }else{
             runWithArguments(parsedCommandMessage.getArguments(), parsedCommandMessage);
         }
     }

     public String getCommand() {
         return command;
     }

    /**
     * Running without arguments.
     * @param parsedCommandMessage
     */
     private void runWithoutArguments(ParsedCommandMessage parsedCommandMessage){
         parsedCommandMessage.setResponse("pong");
         updateCallCount(parsedCommandMessage);
     }

    /**
     * Run with arguments. 
     * @param arguments
     * @param parsedCommandMessage
     */
     private void runWithArguments(List<String[]> arguments, ParsedCommandMessage parsedCommandMessage) {
         for (String[] command : arguments){
             if (command == null){
                 continue;
             }
             switch(command[0]){
                 case "help":
                     parsedCommandMessage.appendToResponse("ping -help -stats");
                     break;
                 case "stats":
                     parsedCommandMessage.appendToResponse(getAmountForUser(parsedCommandMessage));
                     break;
                 default:
                     parsedCommandMessage.appendToResponse("Unknown Command: " + command[0]);

             }
         }
         updateCallCount(parsedCommandMessage);
     }

     private String getAmountForUser(ParsedCommandMessage parsedCommandMessage){
         String idOfUserWhoCalled = parsedCommandMessage.getEvent().getAuthor().getId();
         return mapOfUsersToUsersCalled.getOrDefault(idOfUserWhoCalled, 0).toString();
     }

     private void updateCallCount(ParsedCommandMessage parsedCommandMessage) {
         String idOfUserWhoCalled = parsedCommandMessage.getEvent().getAuthor().getId();
         if (mapOfUsersToUsersCalled.containsKey(idOfUserWhoCalled)) {
             mapOfUsersToUsersCalled.put(idOfUserWhoCalled, mapOfUsersToUsersCalled.get(idOfUserWhoCalled) + 1);
         }else{
             mapOfUsersToUsersCalled.put(idOfUserWhoCalled, 1);
         }
     }
}
