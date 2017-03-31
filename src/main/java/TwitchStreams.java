import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;


public class TwitchStreams extends Command {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if(e.isFromType(ChannelType.TEXT)) {
            String servername = e.getGuild().getName();

            String[] message = e.getMessage().getContent().split(" ");

            if(setAliases(">", "gets").contains(message[0])) {
                if(message.length == 2) {
                    Thread streamThread = new Thread(() -> {
                        Twitch twitch = new Twitch();
                        twitch.setClientId(Main.clientID);

                        /*
                            Gets the streams with that name. For some reason
                            if the stream is offline it won't show up as a stream
                            so I had to fix that by checking if stream was null or
                            not. Written on 27/03/2017
                         */
                        twitch.streams().get(message[1], new StreamResponseHandler() {
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
                                    getStreamChannel(e, twitch, message[1]);
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
                    });
                    streamThread.start();
                } else {
                    e.getChannel().sendMessage(e.getAuthor().getAsMention() + " I need a name to look for on Twitch.").queue();
                }
            }
        }

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
