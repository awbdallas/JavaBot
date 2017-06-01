import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.restaction.order.ChannelOrderAction;

import java.util.Comparator;

/**
 * Need to sort channels Alphabetically in discord. This is the best
 * way to do it. And by best, the way I figured out how how to
 */
public class ChannelSorter {
    /**
     * run is called to run the command channel sorter
     * @param guild Just need the guild to know where this is applying
     * @return Message gets output to the channel it's called
     */
    public static String run(Guild guild) {
        ChannelComparator comparator = new ChannelComparator();
        // I have no idea why one is a text channel and the other is a
        // ChannelType TextChannel. I actually don't.
        ChannelOrderAction<TextChannel> holding = new ChannelOrderAction<>(guild, ChannelType.TEXT);
        holding = holding.sortOrder(comparator);
        holding.queue();
        return "Please stop moving channels. K THX Bye";
    }
}

class ChannelComparator implements Comparator<TextChannel> {
    /**
     * Overrided compare comparing text channels.
     * @param one, two which are both text channels
     * @return -1, 0, 1 I believe. It's just a compare to with strings
     */
    @Override
    public int compare(TextChannel one, TextChannel two){
        return one.getName().compareTo(two.getName());
    }
}

