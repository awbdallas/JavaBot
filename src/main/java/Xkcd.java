import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;


/**
 * Created to work with XKCD stuff
 */
public class Xkcd {
    private int highest_number;

    Xkcd(){
        this.highest_number = getInfo();
    }

    /*
     *
     *
     */
    public String run(ParsedCommandMessage message){
        if (message.getArguments()[0] == "None"){
            File holding = getComic(this.highest_number);
            sendFile(holding, message.getEvent().getChannel());
        }else{
            // Only accepting numbers at the moment. Keywords would be a pain
            try{
                int request_comic_number = Integer.parseInt(message.getArguments()[0]);
                if (request_comic_number > this.highest_number || request_comic_number <= 0){
                    return "Invalid number";
                }
                File file = checkForFile(message.getArguments()[0]);
                if (file != null){
                    sendFile(file, message.getEvent().getChannel());
                }else{
                    file = getComic(request_comic_number);
                }


            }catch(NumberFormatException e){
                return "Argument malformed";
            }

        }
        return "Probably was an error. vOv";
    }

    private File getComic(int comic_number){
        try {
            Document doc = Jsoup.connect("https://www.xkcd.com").get();
            Elements picture = doc.select("img[src$=.png]");
            String image_link = null;
            for (Element element : picture){
                if (element.attr("abs:src").contains("comics")){
                    image_link = element.attr("abs:src");
                }
            }
            if (image_link != null){
                URL url = new URL(image_link);
                InputStream in = new BufferedInputStream(url.openStream());
                OutputStream out = new BufferedOutputStream(new FileOutputStream("test.png"));

                for (int i; (i = in.read()) != -1; ){
                    out.write(i);
                }
                in.close();
                out.close();

                return new File("test.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File checkForFile(String name){
        ClassLoader classLoader = getClass().getClassLoader();
        try{
           File file = new File(classLoader.getResource("XKCD/" + name + ".png").getFile());
           return file;
        }catch(NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    private String sendFile(File file, MessageChannel channel){
        MessageBuilder builder = new MessageBuilder();
        builder.append("Hey, I found it");
        try {
            channel.sendFile(file, builder.build()).queue();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error sending file";
        }
        return "";
    }

    private int getInfo(){
        // TODO make setInfo
        ClassLoader classLoader = getClass().getClassLoader();
        try{
            File file = new File(classLoader.getResource("XKCD/" + "info.txt").getFile());
            // Should only be one line
            BufferedReader br = new BufferedReader(new FileReader(file));
            return Integer.parseInt(br.readLine().trim());
        }catch (NullPointerException e){
            System.err.println("Error with info.txt");
            System.exit(1);
        }catch (FileNotFoundException e){
            e.printStackTrace();
            System.err.println("Error with info.txt");
            System.exit(1);
        }catch (IOException e){
            e.printStackTrace();
        }

        return -1;
    }
}
