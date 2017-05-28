import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener extends ListenerAdapter {
    private MessageCommandParser messageparser;
    private HashMap<String, Message> hm = new HashMap<>();

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
            Pattern pattern;
            Matcher matcher;
            Boolean found = false;

            String[] patterns = new String[]{":(.*?):", //:something here:
            "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})" //urls Grabbed from here: http://stackoverflow.com/a/17773849
            };

            for(int i = 0; i < patterns.length; i++){
                pattern = Pattern.compile(patterns[i]);
                matcher = pattern.matcher(message);
                while (matcher.find()){
                    switch(i){
                        case 0:
                            String[] possible_extensions = new String[]{".gif",".png",".jpg"};
                            Boolean image_found = false;
                            File file = null;
                            ClassLoader classLoader = getClass().getClassLoader();
                            for(String extension : possible_extensions){
                                // Testing for the file
                                try{
                                    file = new File(classLoader.getResource("emojis/" + matcher.group(1) + extension).getFile());
                                }catch(NullPointerException e){
                                    continue;
                                }
                                image_found = true;
                            }
                            if (image_found){
                                MessageBuilder messageBuilder = new MessageBuilder();
                                messageBuilder.append(matcher.group(1));
                                try {
                                    event.getChannel().sendFile(file, messageBuilder.build()).queue();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case 1:
                            // Builds as running
                            Message found_repeat = this.hm.get(matcher.group(0));
                            if (found_repeat != null){
                                // Means the link was used before
                                String found_author = found_repeat.getAuthor().getName();
                                String time_difference = get_message_time_difference(found_repeat, event.getMessage());
                                event.getChannel().sendMessage(String.format("Linked by: %s " +
                                        " %s ago%n", found_author, time_difference)).queue();
                            }else{
                                this.hm.put(matcher.group(0), event.getMessage());
                            }
                        default:
                            break;
                    }
                }
            }

        }
    }

    /**
     * Needed a way to say the difference between times 
     * @param first, second which are both Messages.
     * @return  string with the time and type of the time since between the first and second messages
     */
    public String get_message_time_difference(Message first, Message second){
        // Returns seconds, we're going to convert that to the highest we can
        long seconds_difference = second.getCreationTime().toEpochSecond() - first.getCreationTime().toEpochSecond();

        if(seconds_difference / 86400 >= 1){
            // Days
            return String.format("%d days", seconds_difference / 86400);
        } else if(seconds_difference / 3600 >= 1){
            // Hours
            return String.format("%d hours", seconds_difference / 3600);
        } else if(seconds_difference / 60 >= 1){
            // Minutes
            return String.format("%d minutes", seconds_difference / 60);
        }else{
            // seconds
            return String.format("%d seconds", seconds_difference);
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