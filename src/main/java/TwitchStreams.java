import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.Stream;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TwitchStreams extends Command {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if(e.isFromType(ChannelType.TEXT)) {
            String[] message = e.getMessage().getContent().split(" ");

            String servername = e.getGuild().getName();
            if(!Database.checkForServer(servername)) {
                Main.prefixes.put(servername, ">");
                Database.writeToPrefixes(servername);
            }

            if(setAliases(Main.prefixes.get(servername), "gets").contains(message[0])) {
                if(message.length == 2) {
                    Thread streamThread = new Thread(() -> {
                        Twitch twitch = new Twitch();
                        twitch.setClientId("client ID");

                        twitch.streams().get(message[1], new StreamResponseHandler() {
                            @Override
                            public void onSuccess(Stream stream) {
                                if(stream != null) {
                                    Channel channel = stream.getChannel();
                                    String toSend = channel.getUrl() + " `Status: Live!`";
                                    e.getChannel().sendMessage(toSend).queue();
                                } else {
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
