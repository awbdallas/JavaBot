package listeners;

import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdatePositionEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.restaction.order.ChannelOrderAction;

import java.util.Comparator;

public class ChannelMovedListener extends ListenerAdapter {
    public ChannelMovedListener(){ }

    @Override
    public void onTextChannelUpdatePosition(TextChannelUpdatePositionEvent event){
        System.out.printf("[%s][%s] Channel was moved. Moving it back\n", event.getGuild().getName(),
                event.getChannel().getName());

        ChannelComparator comparator = new ChannelComparator();
        ChannelOrderAction<TextChannel> holding = new ChannelOrderAction<>(event.getGuild(), ChannelType.TEXT);

        holding = holding.sortOrder(comparator);
        holding.queue();
    }
}

class ChannelComparator implements Comparator<TextChannel> {
    ChannelComparator () {

    }
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
