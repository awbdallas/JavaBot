package commands.xkcd;

import commands.structure.Command;
import commands.structure.ParsedCommandMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;

public class Xkcd implements Command {
    private final String command = "xkcd";

    /**
     * run is called by the thing that runs all the commands that's called by the
     * on parsedCommandMessage received
     * @param parsedCommandMessage which is a parsed command so we know it's xkcd
     * @return string that will be the altText of the image or an error parsedCommandMessage
     */
    public void run(ParsedCommandMessage parsedCommandMessage){
        XKCDImage holding;
        if (parsedCommandMessage.getArguments().size() == 0){
            runWithOutArguments(parsedCommandMessage);
        } else {
            runWithArguments(parsedCommandMessage);
        }
        if(parsedCommandMessage.getResponse() == null){
            parsedCommandMessage.setResponse("Error getting image. Please don't hate me.");
        }
    }

    private void runWithArguments(ParsedCommandMessage parsedCommandMessage) {
        for (String[] command : parsedCommandMessage.getArguments()){
            if (command == null){
                continue;
            }
            switch (command[0]){
                case "help":
                    parsedCommandMessage.appendToResponse("xkcd -help -random");
                    break;
                case "random":
                    XKCDImage holding;
                    holding = getComic(true);
                    sendFile(holding.get_image(), parsedCommandMessage.getEvent().getChannel(),
                            holding.get_title());
                    parsedCommandMessage.appendToResponse("Alt Text: " + holding.get_alt_text());
                    break;
                default:
                    parsedCommandMessage.appendToResponse("Command not understood " + command[0]);
            }
        }

    }

    private void runWithOutArguments(ParsedCommandMessage parsedCommandMessage) {
        XKCDImage holding;
        holding = getComic(false);
        sendFile(holding.get_image(), parsedCommandMessage.getEvent().getChannel(),
                holding.get_title());
        parsedCommandMessage.setResponse(holding.get_alt_text());
    }

    /**
     * Get comic which includes opening the request
     * @param random so we know if it's a random request or not
     * @return will return an commands.xkcd.XKCDImage which is more or less acting like a
     * struct containing all the info
     */
    private static XKCDImage getComic(Boolean random){
        String imageLink = null;
        String altText = null;
        String title = null;
        Document doc;

        try {
            if (random){
                doc = Jsoup.connect("https://c.xkcd.com/random/comic/").get();
            }else{
                doc = Jsoup.connect("https://www.xkcd.com").get();
            }
            Elements picture = doc.select("img[src$=.png]");
            for (Element element : picture){
                if (element.attr("abs:src").contains("comics")){
                    imageLink = element.attr("abs:src");
                    altText = element.attr("title");
                    title = element.attr("alt");
                }
            }
            if (imageLink != null){
                URL url = new URL(imageLink);
                InputStream in = new BufferedInputStream(url.openStream());
                OutputStream out = new BufferedOutputStream(
                        new FileOutputStream("/tmp/xkcd.png"));
                for (int i; (i = in.read()) != -1; ){
                    out.write(i);
                }
                in.close();
                out.close();

                return new XKCDImage(new File("/tmp/xkcd.png"), altText, title);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sending the file + title to the channel of choice
     * @param file File of the image that we're sending
     * @param channel Channel to send the message too
     * @param title Title of the image. Could be any text really
     */
    private static void sendFile(File file, MessageChannel channel, String title){
        MessageBuilder builder = new MessageBuilder();
        try {
            builder.append(title);
            channel.sendFile(file, builder.build()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCommand() {
        return command;
    }
}

class XKCDImage {
    private File imageFile;
    private String altText;
    private String title;

    XKCDImage(File imageFile, String altText, String title){
        this.imageFile = imageFile;
        this.altText = altText;
        this.title = title;
    }

    public File get_image(){ return this.imageFile; }
    public String get_alt_text(){ return this.altText; }
    public String get_title(){ return this.title; }
}
