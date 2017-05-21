import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;

public class Xkcd {
    /**
     * run is called by the thing that runs all the commands that's called by the
     * on message recieved
     * @param message which is a parsed command so we know it's xkcd
     * @return string that will be the alt_text of the image or an error message
     */
    public String run(ParsedCommandMessage message){
        XKCD_image holding;
        if (message.getArguments()[0].equals("None")){
            holding = getComic(false);
            sendFile(holding.get_image(), message.getEvent().getChannel(),
                    holding.get_title());
            return holding.get_alt_text();
        }else if(message.getArguments()[0].toLowerCase().equals("random")){
            holding = getComic(true);
            sendFile(holding.get_image(), message.getEvent().getChannel(),
                    holding.get_title());
            return holding.get_alt_text();
        }
        return "I mean, probably was an error somewhere";
    }

    /**
     * Get comic which includes opening the request
     * @param random so we know if it's a random request or not
     * @return will return an XKCD_image which is more or less acting like a
     * struct containing all the info
     */
    private XKCD_image getComic(Boolean random){
        String image_link = null;
        String alt_text = null;
        String title = null;
        Document doc;

        try {
            if (random){
                doc = Jsoup.connect("https://c.xkcd.com/random/comic/").get();
            }else{
                doc = Jsoup.connect("https://www.xkcd.com").get();
            }
            Elements picture = doc.select("img[src$=.png]");
            ClassLoader classLoader = getClass().getClassLoader();
            for (Element element : picture){
                if (element.attr("abs:src").contains("comics")){
                    image_link = element.attr("abs:src");
                    alt_text = element.attr("title");
                    title = element.attr("alt");
                }
            }
            if (image_link != null){
                URL url = new URL(image_link);
                InputStream in = new BufferedInputStream(url.openStream());
                OutputStream out = new BufferedOutputStream(
                        new FileOutputStream("/tmp/xkcd.png"));
                for (int i; (i = in.read()) != -1; ){
                    out.write(i);
                }
                in.close();
                out.close();

                return new XKCD_image(new File("/tmp/xkcd.png"), alt_text, title);
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
    private void sendFile(File file, MessageChannel channel, String title){
        MessageBuilder builder = new MessageBuilder();
        try {
            builder.append(title);
            channel.sendFile(file, builder.build()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class XKCD_image{
    private File image_file;
    private String alt_text;
    private String title;

    XKCD_image(File image_file, String alt_text, String title){
        this.image_file = image_file;
        this.alt_text = alt_text;
        this.title = title;
    }

    public File get_image(){ return this.image_file; }
    public String get_alt_text(){ return this.alt_text; }
    public String get_title(){ return this.title; }

}
