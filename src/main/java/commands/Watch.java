package commands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Watch implements Command {
    private final String command = "watch";

    public void run(ParsedCommandMessage command){
        try{
            String filePathForWatchList = command.getEvent().getClass().getResource("/watch_list.txt").getPath();
            BufferedWriter out = new BufferedWriter(new FileWriter(filePathForWatchList));
            out.write(command.getEvent().getMessage().getAuthor().getId() + "\n");
            out.close();
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Error writing new name to watch file");
            command.setResponse("Error adding your name to the list");
        }
        command.setResponse("Your name has been added to the list");
    }

    public String getCommand(){
        return command;
    }
}
