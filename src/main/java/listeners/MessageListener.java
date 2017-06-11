package listeners;

import commands.MessageCommandParser;
import commands.MessageCommands;
import commands.ParsedCommandMessage;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import utils.Utils;

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
     * the event listener as the listeners.MessageListener. After that it's handed off
     * @param   args  just args for the program
     * @returns none
     */
    public static void main(String[] args)
        throws LoginException, RateLimitedException, InterruptedException{

        String token = Utils.get_env_var("token", true);

        try {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .buildBlocking();
            jda.addEventListener(new MessageListener());
            jda.addEventListener(new VoiceChannelListener());
        } catch (LoginException e){
            // Login to discord error
            e.printStackTrace();
        } catch (InterruptedException e){
            // Only occurs if JDA doesn't load
            System.out.println("Interrupted");
            e.printStackTrace();
        } catch (RateLimitedException e){
            // Discord with rate limit logins
            System.out.println("Rate Limited");
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
        Pattern link_pattern = Pattern.compile(Utils.LINK_REGEX);
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
        Utils.log_command(parsed_command);
        String response = MessageCommands.runCommand(parsed_command);
        event.getChannel().sendMessage(response).queue();
        Utils.log_command(parsed_command, response);
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

        // TODO: Figure out how to only load these once and use them later. Like an act on text object?
        String[] patterns = new String[]{Utils.COMMAND_REGEX, //:something here: group 1 is inner. What we're going for
                Utils.LINK_REGEX // Just checks if it's a link
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
                                Utils.log_text(event, "Found file " + matcher.group(1), "Sending image");
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
                            String time_difference = Utils.get_message_time_difference(found_repeat, event.getMessage());
                            Utils.log_text(event, "Found repeat for " + matcher.group(0), "Calling them out");
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
        // :( No bots plz
        if(event.getAuthor().isBot()){
            return;
        }
        // Private I don't support. I want all the text to be in a public setting(ish)
        if(event.isFromType(ChannelType.PRIVATE)){
            event.getChannel().sendMessage("Bot doesn't support private messages").queue();
            return;
        }

        // Don't deal with attachments because they could be anything
        if (event.getMessage().getAttachments().size() > 0){
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

}