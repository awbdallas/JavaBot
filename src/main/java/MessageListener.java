import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener extends ListenerAdapter {
    private MessageCommandParser messageparser;

    /** Constructor. Mostly just need the message parser at the moment
     *
     */
    public MessageListener(){
        this.messageparser = new MessageCommandParser();
    }

    /** * Main. Starts the program. Mostly just gets token, logs in, and sets
     * the event listener as the MessageListener. After that it's handed off
     * @param   args  just args for the program
     * @returns none
     */
    public static void main(String[] args)
        throws LoginException, RateLimitedException, InterruptedException{

        String token = MessageListener.getToken();

        try {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .buildBlocking();
            jda.addEventListener(new MessageListener());
        } catch (LoginException e){
            // Login to discord error
            e.printStackTrace();
        } catch (InterruptedException e){
            // Only occurs if JDA doesn't load
            e.printStackTrace();
        } catch (RateLimitedException e){
            // Discord with rate limit logins
            e.printStackTrace();
        }
    }

    /**
     * Trigger for the rest of the program.
     * @param   event a MessageReceievedEvent that's triggered on the bot
     *                getting a new message
     * @returns none
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        ParsedCommandMessage parsed_command = this.messageparser.parseMessage(event.getMessage());
        if (parsed_command != null){
            if (!event.isFromType(ChannelType.PRIVATE)){
                System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                        event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                        event.getMessage().getContent());
                parsed_command.setEvent(event);
                String response = MessageCommands.runCommand(parsed_command);
                event.getChannel().sendMessage(response).queue();
            }
        }else{
            // If it's not a command, there's still a few things it can be
            // We might call this keywords. Unsure at the moment
            String message = event.getMessage().getContent();

            Pattern pattern = Pattern.compile(":(.*?):");
            Matcher matcher = pattern.matcher(message);
            Boolean found = false;
            String what_was_found = "";

            while (matcher.find()) {
                // Every other group is what we want
                what_was_found = matcher.group(1);
                found = true;
            }
            if (found){
                String[] possible_extensions = new String[]{".gif",".png",".jpg"};
                Boolean image_found = false;
                File file = null;

                for(String extension : possible_extensions){
                    // Testing for the file
                    ClassLoader classLoader = getClass().getClassLoader();
                    try{
                        file = new File(classLoader.getResource("emojis/" +what_was_found + extension).getFile());
                    }catch(NullPointerException e){
                       continue;
                    }
                    image_found = true;

                }
                if (image_found){
                    MessageBuilder messageBuilder = new MessageBuilder();
                    messageBuilder.append(what_was_found);
                    try {
                        event.getChannel().sendFile(file, messageBuilder.build()).queue();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                }

        }
    }

    /**
     * Returns the token for this program that's stored in
     * environment variables. If variable is not set, will exit.
     *
     * @return  string token from env.
     */
    public static String getToken(){
        String token = System.getenv("token");
        if (token == null){
            System.err.println("Token environment variable not set");
            System.exit(1);
        }
        return token;
    }
}