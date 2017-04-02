import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleSearch extends Command {

    private final List<String> aliases = setAliases(">", "g");


    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        String[] message = e.getMessage().getContent().split(" ");

        if(e.isFromType(ChannelType.TEXT)) {

            String servername = e.getGuild().getName();

            if(aliases.contains(message[0])) {
                List<String> parameters = Methods.getParameters(message);
                String query = Methods.removeParamUrlEncoded(message);

                MessageChannel channel = e.getChannel();
                User author = e.getAuthor();

                if(parameters.size() > 0) {
                    for(String param : parameters) {
                        switch (param) {
                            case "--q":
                                String link = Methods.queryGoogle(query);
                                Methods.queueTrack(e, link, servername);
                                break;
                        }
                    }
                    return;
                } else if (parameters.size() == 0){
                    String encodedQuery = Methods.encodeString(query);

                    long start = System.currentTimeMillis();
                    String link = Methods.queryGoogle(encodedQuery);
                    long end = System.currentTimeMillis();
                    long fTime = end - start;

                    channel.sendMessage(link + " `" + fTime + "ms`").queue();
                } else {
                    channel.sendMessage(author.getAsMention() + " You need to give me some words to search after.").queue();
                }

            }
        } else if (message[0].equals(">g") && e.isFromType(ChannelType.PRIVATE)){
            e.getChannel().sendMessage("That command doesn't work here YET").queue();
        }

    }


}
