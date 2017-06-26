import listeners.ChannelMovedListener;
import listeners.MessageListener;
import listeners.VoiceChannelListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import utils.Utils;

import javax.security.auth.login.LoginException;

public class JavaBot {

    /** Main. Starts the program. Mostly just gets token, logs in, and sets
     * the event listener as the listeners.MessageListener. After that it's handed off
     * @param   args  just args for the program
     * @returns none
     */
    public static void main(String[] args)
            throws LoginException, RateLimitedException, InterruptedException{

        String token = Utils.getEnvVar("token", true);

        try {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .buildBlocking();
            jda.addEventListener(new MessageListener(jda));
            jda.addEventListener(new VoiceChannelListener());
            jda.addEventListener(new ChannelMovedListener());
        } catch (LoginException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            System.out.println("Interrupted");
            e.printStackTrace();
        } catch (RateLimitedException e){
            System.out.println("Rate Limited");
            e.printStackTrace();
        }
    }
}
