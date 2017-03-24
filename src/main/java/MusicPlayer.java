import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;


public class MusicPlayer extends Command {
    /*
        This is the first attempt at a voice/music
        player. It will be choppy and what not but I
        will try and improve it as I go on.
        Written on 22/03/
     */

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        /*
            getRawContent() is faster than getContent() because
            no modifications are made to the message content.
            Written on 22/03/2017
         */
        String servername = e.getGuild().getName();
        if(!Database.checkForServer(servername) && (!servername.equals("PRIVATE"))) {
            Main.prefixes.put(servername, ">");
            Database.writeToPrefixes(servername);
        }

        String[] message = e.getMessage().getContent().split(" ");
        if(setAliases(Main.prefixes.get(servername), "join").contains(message[0])) {
            Guild guild = e.getGuild();
            User author = e.getAuthor();
            String channelName;
            /*
                This will be the first voice channel
                with the name, not case sensitive
            */
            if (message.length > 1) {
                channelName = message[1];
            } else {
                e.getChannel().sendMessage(author.getAsMention() + " Please specify a channel to join.").queue();
                return;
            }
            /*
                Might wanna check if the channel exists
                and send out a message if it doesn't
                Written on 22/03/2017
             */
            VoiceChannel channel = guild.getVoiceChannelsByName(channelName, true).get(0);
            AudioManager manager = guild.getAudioManager();
            AudioPlayer player = Main.playerManager.createPlayer();
            TrackScheduler trackScheduler = new TrackScheduler(player);
            player.addListener(trackScheduler);

            manager.setSendingHandler(new AudioPlayerSendHandler(player));
            manager.openAudioConnection(channel);

            Main.playerManager.loadItem("https://www.youtube.com/playlist?list=PL9GDpEaemvz7crT3RNl-ffvuoIC1-KpAi", new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    trackScheduler.queue(audioTrack);
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    audioPlaylist.getTracks().forEach(trackScheduler::queue);
                }

                @Override
                public void noMatches() {
                    System.out.println("NO MATCHES");
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    System.out.println("LOAD FAILED");
                }
            });
        }
        /*
            For leaving the voice channel for the specified server
         */
        else if(setAliases(Main.prefixes.get(servername), "leave").contains(message[0])) {
            Guild guild = e.getGuild();

            AudioManager manager = guild.getAudioManager();
            manager.closeAudioConnection();


        }


    }

}
