import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener extends ListenerAdapter {
    private MessageCommandParser messageparser;
    private HashMap<String, Message> hm = new HashMap<>();
    private int got_history = 0;

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

        String token = MessageListener.get_env_var("token", true);

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
     * Build link history is to build up hm which is a hash map of all the links with messages
     * which is needed for checking if a link was used.
     * @param   event
     * @returns none
     */
    public void build_link_history(MessageReceivedEvent event){
        event.getChannel().sendMessage("Warming up").queue();
        List<TextChannel> bot_channels = event.getJDA().getTextChannels();
        String link_regex = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})";
        Pattern link_pattern = Pattern.compile(link_regex);
        for (TextChannel textchannel : bot_channels){
            int check_amount = 1000;
            for (Message message : textchannel.getIterableHistory()){
                Matcher matcher_holding = link_pattern.matcher(message.getContent());
                while(matcher_holding.find()){
                    // Don't want to override current ones. It iterates backwards. As a result of this
                    // only the last time it linked will be brought up (not first time)
                    this.hm.put(matcher_holding.group(), message);
                }
                if (check_amount-- <= 0) break;
            }
        }
        event.getChannel().sendMessage("Done").queue();
        this.got_history = 1;
    }

    /**
     * Well, we know it's possibly a command, so we're going to act on the command
     * by giving it a parsed command and go from there.
     * @param   parsed_command
     * @returns none
     */
    public void act_on_command(ParsedCommandMessage parsed_command){
        MessageReceivedEvent event = parsed_command.getEvent();
        log_command(parsed_command);
        String response = MessageCommands.runCommand(parsed_command);
        event.getChannel().sendMessage(response).queue();
        log_command(parsed_command, response);
    }

    /**
     * Log command is just to output. May be changed later to actually have a log, but this
     * would be easier to change in the future
     * @param   parsed_command
     * @returns none
     */
    public static void log_command(ParsedCommandMessage parsed_command){
        MessageReceivedEvent event = parsed_command.getEvent();
        System.out.printf("[%s][%s] Command: %s Arguments: \n", event.getGuild(), event.getTextChannel(),
                parsed_command.getCommand(), parsed_command.arguments_to_string());
    }

    /**
     * Log command is just to output. May be changed later to actually have a log, but this
     * would be easier to change in the future. This one also gives out result
     * @param   parsed_command, result
     * @returns none
     */
    public static void log_command(ParsedCommandMessage parsed_command, String result){
        MessageReceivedEvent event = parsed_command.getEvent();
        System.out.printf("[%s][%s] Result: \n", event.getGuild().getName(),
                event.getTextChannel().getName(), result);
    }

    /**
     * Way to output easily based on text rather than a command
     * @param   event, found, result
     * @returns none
     */
    public static void log_text(MessageReceivedEvent event, String found, String result){
        System.out.printf("[%s][%s] Found: %s Result: %s\n", event.getGuild().getName(), event.getTextChannel().getName(),
                found, result);
    }

    /**
     * Act on text is if there isn't a command we're going to act on text to see
     * if there's something that's in the text. Might end up changing this in the
     * future
     * @param   event
     * @returns none
     */
    public void act_on_text(MessageReceivedEvent event){
        // If it's not a command, there's still a few things it can be
        // We might call this keywords. Unsure at the moment
        String message = event.getMessage().getContent();
        Pattern pattern;
        Matcher matcher;

        String[] patterns = new String[]{":(.*?):", //:something here: group 1 is inner. What we're going for
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
                                log_text(event, "Found file " + matcher.group(1), "Sending image");
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
                            log_text(event, "Found repeat for " + matcher.group(0), "Calling them out");
                            event.getChannel().sendMessage(String.format("Last Linked by: %s " +
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

    /**
     * Trigger for the rest of the program.
     * @param   event a MessageReceievedEvent that's triggered on the bot
     *                getting a new message
     * @returns none
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        // Private I don't support. I want all the text to be in a public setting(ish)
        if(event.isFromType(ChannelType.PRIVATE)){
            event.getChannel().sendMessage("Bot doesn't support private messages").queue();
            return;
        }
        // Kinda basic logging. Not doing to a file. Just output stream atm
        // Could do that in the future, but not sure
        System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                event.getMessage().getContent());
        // Few reasons this is here. Main is that I can't reference from a static context
        // and this is the first chance I'll get to build the history, other is because it allows a
        // after each
        if (this.got_history == 0){
            build_link_history(event);
        }

        ParsedCommandMessage parsed_command = this.messageparser.parseMessage(event);
        if (parsed_command != null){
            act_on_command(parsed_command);
        }else{
            act_on_text(event);
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
     * Returns requested env var. Will fail if you want it too
     * @param envvar (variable you want) fail (want it to fail?)
     * @return  string token from env.
     */
    public static String get_env_var(String envvar, boolean fail){
        String var = System.getenv(envvar);
        if (var == null){
           System.err.println(String.format("Environment variable: %s not set", envvar));
           if (fail){
               System.exit(1);
           }
        }
       return var;
    }
}