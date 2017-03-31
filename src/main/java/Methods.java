import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public class Methods {

    /*
        TODO Properly comment the entire class ASAP
     */

    public static void leaveVoice(Guild g) {
        AudioManager manager = g.getAudioManager();
        manager.closeAudioConnection();
    }



    public static void playingTrack(MessageReceivedEvent e, String servername) {
        MessageChannel channel = e.getChannel();
        User author = e.getAuthor();

        TrackScheduler trackScheduler = Main.schedulers.get(servername);
        String playing = trackScheduler.getPlayer().getPlayingTrack().getInfo().title;
        channel.sendMessage(author.getAsMention() + " " + playing).queue();
    }



    public static void resumePlayer(MessageReceivedEvent e, String servername) {
        MessageChannel channel = e.getChannel();
        User author = e.getAuthor();

        TrackScheduler trackScheduler = Main.schedulers.get(servername);
        trackScheduler.getPlayer().setPaused(false);
        channel.sendMessage(author.getAsMention() + " Player running.").queue();
    }



    public static void pausePlayer(MessageReceivedEvent e, String servername) {
        MessageChannel channel = e.getChannel();
        User author = e.getAuthor();

        TrackScheduler trackScheduler = Main.schedulers.get(servername);
        trackScheduler.getPlayer().setPaused(true);
        channel.sendMessage(author.getAsMention() + " Player paused.").queue();
    }



    public static void getNext(String servername) {
        TrackScheduler trackScheduler = Main.schedulers.get(servername);
        trackScheduler.nextTrack();
    }



    public static void queueTrack(MessageReceivedEvent e, String song, String servername) {
        MessageChannel channel = e.getChannel();
        User author = e.getAuthor();
        TrackScheduler trackScheduler = Main.schedulers.get(servername);

        if(trackScheduler == null) {
            channel.sendMessage(author.getAsMention() + " I need to be in a voice channel first.").queue();
            return;
        }

        Main.playerManager.loadItem(song, new AudioLoadResultHandler() {
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
                e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Make sure the playlist/song isn't private.").queue();
            }

            @Override
            public void loadFailed(FriendlyException ex) {
                e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Something happened and I couldn't load what you provided").queue();
            }
        });
    }
}
