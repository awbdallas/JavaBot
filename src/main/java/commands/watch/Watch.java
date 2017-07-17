package commands.watch;

import commands.structure.Command;
import commands.structure.ParsedCommandMessage;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Watch implements Command {
    private final String command = "watch";
    private Map<Long, List<Long>> mapOfIdsToListsOfWhosWatchingForThem;
    private List<Long> listOfUsersWatchingForAll;

    public Watch() {
       mapOfIdsToListsOfWhosWatchingForThem = new HashMap<>();
       listOfUsersWatchingForAll = new ArrayList<>();
    }

    public void run(ParsedCommandMessage parsedCommandMessage){
        if (parsedCommandMessage.getArguments().size() != 0){
            runWithArguments(parsedCommandMessage);
        } else {
            runWithoutArguments(parsedCommandMessage);
        }
    }

    public void runWithArguments(ParsedCommandMessage parsedCommandMessage) {
        parsedCommandMessage.setResponse("Your name has been added to the list");
        for (User user: parsedCommandMessage.getEvent().getMessage().getMentionedUsers()){
           appendWatchingUserToList(parsedCommandMessage.getEvent().getMessage().getAuthor().getIdLong(),
                   user.getIdLong());
        }

        for (String[] arguments : parsedCommandMessage.getArguments()){
            if (arguments == null || arguments.toString().contains("@")){
                continue;
            }
            switch(arguments[0]){
                case "help":
                    parsedCommandMessage.appendToResponse("watch @metnions... -help");
                    break;
                default:
                    parsedCommandMessage.appendToResponse("Unable to process argument: " + arguments[0]);
            }
        }
    }

    public void runWithoutArguments(ParsedCommandMessage parsedCommandMessage) {
        listOfUsersWatchingForAll.add(parsedCommandMessage.getEvent().getAuthor().getIdLong());
        parsedCommandMessage.setResponse("Your name has been added to the list");
    }

    public String getCommand(){
        return command;
    }

    public void clearUsersWatchingAll() {
        listOfUsersWatchingForAll.clear();
    }

    public List<Long> getUsersWatchingallList() {
        return listOfUsersWatchingForAll;
    }

    public List<Long> getListOfUsersToMessage(Long userID){
        return new ArrayList<>();
    }

    private void appendWatchingUserToList(Long watchingUser, Long userToWatch) {
        if (mapOfIdsToListsOfWhosWatchingForThem.get(userToWatch) != null){
            mapOfIdsToListsOfWhosWatchingForThem.get(userToWatch).add(watchingUser);
        } else {
            List<Long> newListForUser = new ArrayList<>();
            newListForUser.add(watchingUser);
            mapOfIdsToListsOfWhosWatchingForThem.put(userToWatch, newListForUser);
        }
    }
}
