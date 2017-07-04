package listeners;

import commands.MessageCommandParser;
import commands.MessageCommands;
import commands.ParsedCommandMessage;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener extends ListenerAdapter {
    private MessageCommandParser messageCommandParser;
    private HashMap<String, Message> hm = new HashMap<>();
    private MessageCommands messageCommands;

    public MessageListener(JDA jda){
        this.messageCommandParser = new MessageCommandParser();
        buildLinkHistory(jda);
        messageCommands = new MessageCommands();
    }


    /**
     * Build link history is to build up hm which is a hash map of all the links with messages
     * which is needed for checking if a link was used.
     * @param   jda
     * @returns none
     */
    public  void buildLinkHistory(JDA jda){
        List<TextChannel> botChannels = jda.getTextChannels();
        Pattern linkPattern = Pattern.compile(Utils.LINK_REGEX);
        for (TextChannel textchannel : botChannels){
            int checkAmount = 1000;
            for (Message message : textchannel.getIterableHistory()){
                Matcher matcherHolding = linkPattern.matcher(message.getContent());
                while(matcherHolding.find()){
                    this.hm.put(matcherHolding.group(), message);
                }
                if (checkAmount-- <= 0) break;
            }
        }
    }

    /**
     * Well, we know it's possibly a command, so we're going to act on the command
     * by giving it a parsed command and go from there.
     * @param   parsedCommand
     * @returns none
     */
    public void actOnCommand(ParsedCommandMessage parsedCommand){
        MessageReceivedEvent event = parsedCommand.getEvent();
        Utils.logCommand(parsedCommand);
        messageCommands.runCommand(parsedCommand);
        event.getChannel().sendMessage(parsedCommand.getResponse()).queue();
        Utils.logCommand(parsedCommand, parsedCommand.getResponse());
    }

    /**
     * Act on text is if there isn't a command we're going to act on text to see
     * if there's something that's in the text. Might end up changing this in the
     * future
     * @param   event
     * @returns none
     */
    public void actOnText(MessageReceivedEvent event){
        String message = event.getMessage().getContent();
        Pattern pattern;
        Matcher matcher;

        String[] patterns = new String[]{Utils.COMMAND_REGEX, // something here: group 1 is inner. What we're going for
                Utils.LINK_REGEX // Just checks if it's a link
        };

        for(int i = 0; i < patterns.length; i++){
            pattern = Pattern.compile(patterns[i]);
            matcher = pattern.matcher(message);
            while (matcher.find()){
                switch(i){
                    case 0:
                        String[] possible_extensions = new String[]{".gif",".png",".jpg"};
                        Boolean imageFound = false;
                        File file = null;
                        ClassLoader classLoader = getClass().getClassLoader();
                        for(String extension : possible_extensions){
                            // Testing for the file
                            try{
                                file = new File(classLoader.getResource("emojis/" + matcher.group(1) + extension).getFile());
                            }catch(NullPointerException e){
                                continue;
                            }
                            imageFound = true;
                        }
                        if (imageFound){
                            MessageBuilder messageBuilder = new MessageBuilder();
                            messageBuilder.append(matcher.group(1));
                            try {
                                event.getChannel().sendFile(file, messageBuilder.build()).queue();
                                Utils.logText(event, "Found file " + matcher.group(1), "Sending image");
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
                            String foundAuthor = found_repeat.getAuthor().getName();
                            String messageTimeDifference = Utils.getMessageTimeDifference(found_repeat, event.getMessage());
                            Utils.logText(event, "Found repeat for " + matcher.group(0), "Calling them out");
                            event.getChannel().sendMessage(String.format("Last Linked by: %s " +
                                    " %s ago%n", foundAuthor, messageTimeDifference)).queue();
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
        if(event.getAuthor().isBot()){
            return;
        }

        if(event.isFromType(ChannelType.PRIVATE)){
            event.getChannel().sendMessage("Bot doesn't support private messages").queue();
            return;
        }

        // Don't deal with attachments because they could be anything
        if (event.getMessage().getAttachments().size() > 0){
            return;
        }

        // TODO: Move all logging into a file of some sort. At the moment it's all STDOUT
        System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                event.getMessage().getContent());
        ParsedCommandMessage parsedCommandMessage = this.messageCommandParser.parseMessage(event);
        if (parsedCommandMessage != null){
            actOnCommand(parsedCommandMessage);
        }else{
            actOnText(event);
        }
    }

    public MessageCommands getMessageCommands() {
        return messageCommands;
    }

}