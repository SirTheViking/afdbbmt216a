import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;


public class TwitchStreams extends Command {

    private final List<String> aliases = setAliases(">", "gets", "get-stream", "gs");

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        String[] message = e.getMessage().getContent().split(" ");

        if(aliases.contains(message[0])) {
            MessageChannel channel = e.getChannel();
            User author = e.getAuthor();

            if(e.isFromType(ChannelType.TEXT)) {
                if(message.length == 2) {
                        Twitch twitch = new Twitch();
                        twitch.setClientId(Main.clientID);
                        getStream(e, twitch, message[1]);
                } else {
                    channel.sendMessage(author.getAsMention() + " I need a name to look for on Twitch.").queue();
                }
            }

            else if (e.isFromType(ChannelType.PRIVATE)) {
                if(message.length == 2) {
                    Twitch twitch = new Twitch();
                    twitch.setClientId(Main.clientID);
                    getStream(e, twitch, message[1]);
                } else {
                    channel.sendMessage(author.getAsMention() + " I need a name to look for on Twitch.").queue();
                }
            }
        }

    }




    /**
     * Gets the stream by the name. If it's offline it
     * runs the getChannelMethod in order to get the channel
     * because that's how the Twitch API wrapper works.
     * @param e The Discord library even containing all message information
     * @param t The twitch instance
     * @param name The name of the streamer to look for
     */
    private void getStream(MessageReceivedEvent e, Twitch t, String name) {
        t.streams().get(name, new StreamResponseHandler() {
            @Override
            public void onSuccess(Stream stream) {
                if(stream != null) {
                    Channel channel = stream.getChannel();
                    String toSend = channel.getUrl() + " `Status: Live!`";
                    e.getChannel().sendMessage(toSend).queue();
                } else {
                    /*
                      If the stream was offline I had to run new checks
                      but this time after the channel name instead, and
                      then send that directly through to discord.
                      Written on 27/03/2017
                    */
                    getStreamChannel(e, t, name);
                }
            }

            @Override
            public void onFailure(int i, String s, String s1) {
                e.getChannel().sendMessage(e.getAuthor().getAsMention() + " I couldn't find anything with the name that you provided.").queue();
            }

            @Override
            public void onFailure(Throwable throwable) {
                e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Twitch seems to have given me an error, please try again later.").queue();
            }
        });
    }


    /**
     * This method is used to get the channel in case the stream
     * people are looking for is offline
     * @param e The library event containing all the message information
     * @param t The twitch object used to get twitch data
     * @param name The streamer name provided by the user
     */
    private void getStreamChannel(MessageReceivedEvent e, Twitch t, String name) {

        t.channels().get(name, new ChannelResponseHandler() {
            @Override
            public void onSuccess(Channel channel) {
                String toSend = channel.getUrl() + " `Status: Offline.`";
                e.getChannel().sendMessage(toSend).queue();
            }

            @Override
            public void onFailure(int i, String s, String s1) {
                e.getChannel().sendMessage(e.getAuthor().getAsMention() + " I couldn't find anything with the name that you provided.").queue();
            }

            @Override
            public void onFailure(Throwable throwable) {
                e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Twitch seems to have given me an error, please try again later.").queue();
            }
        });
    }

}
