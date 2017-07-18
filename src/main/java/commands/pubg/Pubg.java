package commands.pubg;

import commands.structure.Command;
import commands.structure.ParsedCommandMessage;
import pro.lukasgorny.core.JPubg;
import pro.lukasgorny.dto.Player;
import pro.lukasgorny.dto.Stat;
import pro.lukasgorny.enums.PUBGMode;
import pro.lukasgorny.enums.PUBGStat;
import pro.lukasgorny.exceptions.ApiException;
import pro.lukasgorny.factory.JPubgFactory;
import utils.Utils;

public class Pubg implements Command {
    public static final String command = "pubg";
    private JPubg jpug;


    public Pubg() {
        jpug = JPubgFactory.getWrapper(Utils.getEnvVar("trn-api-key", true));
    }

    public void run(ParsedCommandMessage parsedCommandMessage) {
        if (parsedCommandMessage.getArguments().size() == 0){
            runWithoutArguments(parsedCommandMessage);
        } else {
            runWithArguments(parsedCommandMessage);
        }
    }

    private void runWithArguments(ParsedCommandMessage parsedCommandMessage) {
        for (String[] arguments : parsedCommandMessage.getArguments()){
            if (arguments == null){
                continue;
            }
            switch(arguments[0]){
                case "help":
                    parsedCommandMessage.appendToResponse("-help -player Player name1, playername 2");
                    break;
                    // TODO: Going to have to refactor this later to account for looking up custom stats
                case "player":
                    for (String player : arguments) {
                        if (player != arguments[0]) {
                            parsedCommandMessage.appendToResponse(lookupUserStatSummary(player, PUBGMode.solo));
                        }
                    }
                    break;
                default:
                    parsedCommandMessage.appendToResponse("Not a valid command");
                    break;
            }
        }
    }

    private String lookupUserStatSummary(String name, PUBGMode gameMode) {
        try {
            Player player = jpug.getByNickname(name, gameMode);
            Stat stat = jpug.getPlayerMatchStatByStatName(player, PUBGStat.RATING);
            return String.format("%s\nRating: %s\nPercentile: %s\nRank: %s\n", name, stat.getDisplayValue(),
                    stat.getPercentile(), stat.getRank());
        } catch (ApiException api) {
            return "Issue looking up user: " + name;
        }

    }

    private void runWithoutArguments(ParsedCommandMessage parsedCommandMessage) {
        parsedCommandMessage.setResponse("So, what if I told you that I need arguments to run?");
    }

    public String getCommand() {
        return command;
    }
}
