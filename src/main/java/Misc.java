import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;


public class Misc extends Command {

    /*
        This entire class is purely for messing
        with things or just having a bunch of small
        things (that don't deserve an entire class) bundled
        together. Written on 20/03/2017
     */

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        /*
            The most common variables will always
            be declared at the top. I will attempt to
            keep this pattern up throughout the document.
            Written on 20/03/2017
         */
        User author = e.getAuthor();
        MessageChannel channel = e.getChannel();
        JDA jda = e.getJDA();

        String message = e.getMessage().getContent();
        /*
            This is for unflipping the table every time
            somebody, flips a table, in the
            chat. Written on 20/03/2017
         */
        if(message.startsWith("(╯") || message.contains("┻")) {
            channel.sendMessage("┬─┬ノ( ゜-゜ノ) " + author.getAsMention() + " put that shit down").queue();
        }

        if(setAliases(">", "ping").contains(message)) {
            channel.sendMessage("Pong! `" + jda.getPing() + "ms`").queue();
        }
    }
}
