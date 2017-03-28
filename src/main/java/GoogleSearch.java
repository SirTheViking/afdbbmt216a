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

public class GoogleSearch extends Command {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    private static final String GOOGLE_URL = "https://google.com/search?q=";

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        String[] message = e.getMessage().getContent().split(" ", 2);

        if(e.isFromType(ChannelType.TEXT)) {

            String servername = e.getGuild().getName();

            if(setAliases(Main.prefixes.get(servername), "g").contains(message[0])) {
                MessageChannel channel = e.getChannel();
                User author = e.getAuthor();
                // If a link was provided
                if (message.length == 2) {
                    final String encodedQuery = encodeString(message[1]);
                /*
                    Get the first page of the returned
                    google search results and retrieve all
                    links from it. Written on 26/03/2017
                 */
                    try {
                        long start = System.currentTimeMillis();
                        final Elements els = Jsoup.connect(GOOGLE_URL + encodedQuery)
                                .userAgent(USER_AGENT)
                                .get()
                                .select("h3.r a");
                        long end = System.currentTimeMillis();
                        long fTime = end - start;
                    /*
                        This only gets the first result.
                        The user should be able to choose how many
                        results they want. min/default = 1, max = 10.
                        Written on 26/03/2017
                     */
                        Element el = els.get(0);
                        channel.sendMessage(el.attr("href") + " `" + fTime + "ms`").queue();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    channel.sendMessage(author.getAsMention() + " You need to give me some words to search after.").queue();
                }
            }
        } else if (message[0].equals(">g") && e.isFromType(ChannelType.PRIVATE)){
            e.getChannel().sendMessage("That command doesn't work here YET").queue();
        }

    }


    /**
     * Used to encode the query that will be sent
     * to google as a search query
     * @param s the string to encode
     * @return the encoded string
     */
    private static String encodeString(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return "error";
    }

}
