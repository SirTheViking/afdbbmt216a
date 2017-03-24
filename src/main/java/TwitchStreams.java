import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TwitchStreams extends Command {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        respondToMessage(e);
    }

    private void respondToMessage(MessageReceivedEvent e) {
        /*
            The most common variables will always
            be declared at the top. I will attempt to
            keep this pattern up throughout the document.
            Written on 20/03/2017
         */
        MessageChannel channel = e.getChannel();
        String mention = e.getAuthor().getAsMention();

        String servername = "PRIVATE"; //This is temporary
        if(e.isFromType(ChannelType.TEXT)) {
            servername = e.getGuild().getName();
        }
        /*
            As every command class will eventually contain this
            It checks whether or not the HashMap already contains
            the server that the message is being sent from. If not
            then it adds it. Written on 21/03/2017
         */
        if(!Database.checkForServer(servername) && (!servername.equals("PRIVATE"))) {
            Main.prefixes.put(servername, ">");
            Database.writeToPrefixes(servername);
        }

        String[] message = e.getMessage().getContent().split(" ");
        /*
            This part is used to get stream links
            Written on 21/03/2017
         */
        if(setAliases(Main.prefixes.get(servername), "get").contains(message[0])) {
            Thread getStream = new Thread(() -> {
                String streamLink = Database.getTwitchStream(message[1]);
                channel.sendMessage(mention + " " + streamLink).queue();
            });
            getStream.start();
        }
        /*
            This part is used to upload stream links
            and usernames with the command format
            >add streamer_name stream_link. Written on 21/03/2017
         */
        else if(setAliases(Main.prefixes.get(servername), "add").contains(message[0])) {
            Thread addStream = new Thread(() -> {
                if(message.length == 2) {
                    String stream_link = message[1];
                    String response = Database.addTwitchStream(stream_link);

                    channel.sendMessage(mention + response).queue();
                }
            });
            addStream.start();
        }
    }

}
