package commands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Watch {
    Watch () { }

    public static String run(ParsedCommandMessage command){
        try{
            String filePathForWatchList = command.getEvent().getClass().getResource("/watch_list.txt").getPath();
            BufferedWriter out = new BufferedWriter(new FileWriter(filePathForWatchList));
            out.write(command.getEvent().getMessage().getAuthor().getId() + "\n");
            out.close();
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Error writing new name to watch file");
            return "There was an error adding your name to the list";
        }
        return "Your name has been added to the list";
    }
}
