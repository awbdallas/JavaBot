import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.Route;

import javax.security.auth.login.LoginException;

public class MessageListener extends ListenerAdapter {
    MessageParser messageparser;

    /** Constructor. Mostly just need the message parser at the moment
     *
     */
    public MessageListener(){
        this.messageparser = new MessageParser();
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
        // TODO actions with parsed_message. At the moment we got it here
        // but we've got to go from there.
        ParsedMessage parsed_message = this.messageparser.parseMessage(event.getMessage());
        if (parsed_message != null){
            if (event.isFromType(ChannelType.PRIVATE)){
                System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                        event.getMessage().getContent());
            }else{
                System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                        event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                        event.getMessage().getContent());
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


