import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.*;
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

        if(e.isFromType(ChannelType.TEXT)) {

            MessageChannel channel = e.getChannel();
            User author = e.getAuthor();

            String servername = e.getGuild().getName();

            String[] message = e.getMessage().getContent().split(" ");
            if(setAliases(Main.prefixes.get(servername), "join").contains(message[0])) {
                Guild guild = e.getGuild();
                String channelName;
            /*
                This will be the first voice channel
                with the name, not case sensitive
            */
                if (message.length > 1) {
                    channelName = message[1];
                } else {
                    channel.sendMessage(author.getAsMention() + " Please specify a channel to join.").queue();
                    return;
                }
            /*
                Might wanna check if the channel exists
                and send out a message if it doesn't
                Written on 22/03/2017
             */
                VoiceChannel vchannel = guild.getVoiceChannelsByName(channelName, true).get(0);
                AudioManager manager = guild.getAudioManager();
                AudioPlayer player = Main.playerManager.createPlayer();
                TrackScheduler trackScheduler = new TrackScheduler(player);

                Main.schedulers.put(servername, trackScheduler);

                player.addListener(trackScheduler);

                manager.setSendingHandler(new AudioPlayerSendHandler(player));
                manager.openAudioConnection(vchannel);
            }

            /*
                Queues a song from a link, or a playlist
                Maybe you can mix SoundCloud and YouTube idk YET.
                Written on 26/03/2017
             */
            else if(setAliases(Main.prefixes.get(servername), "q").contains(message[0])) {
                TrackScheduler trackScheduler = Main.schedulers.get(servername);
                String song = "https://www.youtube.com/playlist?list=PL9GDpEaemvz7crT3RNl-ffvuoIC1-KpAi";

                if(message.length == 2) {
                    song = message[1];
                }

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

            /*
                Move forward into the playlist and
                play the next song
                Written on 26/03/2017
             */
            else if(setAliases(Main.prefixes.get(servername), "next").contains(message[0])) {
                TrackScheduler trackScheduler = Main.schedulers.get(servername);
                trackScheduler.nextTrack();
            }

            /*
                Pause the player.
                Written on 26/03/2017
             */
            else if(setAliases(Main.prefixes.get(servername), "pause").contains(message[0])) {
                TrackScheduler trackScheduler = Main.schedulers.get(servername);
                trackScheduler.getPlayer().setPaused(true);
                channel.sendMessage(author.getAsMention() + " Player paused.").queue();
            }

            /*
                Resume the player.
                Written on 26/03/2017
             */
            else if(setAliases(Main.prefixes.get(servername), "play").contains(message[0])) {
                TrackScheduler trackScheduler = Main.schedulers.get(servername);
                trackScheduler.getPlayer().setPaused(false);
                channel.sendMessage(author.getAsMention() + " Player running.").queue();
            }

            /*
                Get the song that's playing.
                Written on 26/03/2017
             */
            else if(setAliases(Main.prefixes.get(servername), "np").contains(message[0])) {
                TrackScheduler trackScheduler = Main.schedulers.get(servername);
                String playing = trackScheduler.getPlayer().getPlayingTrack().getInfo().title;
                channel.sendMessage(author.getAsMention() + " " + playing).queue();
            }

            /*
                Leave the channel that the bot is currently in.
                Written on 26/03/2017
             */
            else if(setAliases(Main.prefixes.get(servername), "leave").contains(message[0])) {
                Guild guild = e.getGuild();

                AudioManager manager = guild.getAudioManager();
                manager.closeAudioConnection();
            }

        }


    }

}
