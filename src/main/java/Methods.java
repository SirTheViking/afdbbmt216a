import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Methods {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    private static final String GOOGLE_URL = "https://google.com/search?q=";
    private static final String SOUNDCLOUD_URL = "https://soundcloud.com/search/";

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
        String link = trackScheduler.getPlayer().getPlayingTrack().getInfo().uri;

        channel.sendMessage(author.getAsMention() + " " + link).queue();
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


    public static void getDuration(MessageReceivedEvent e, String servername) {
        MessageChannel channel = e.getChannel();
        User author = e.getAuthor();

        TrackScheduler trackScheduler = Main.schedulers.get(servername);
        AudioPlayer player = trackScheduler.getPlayer();
        long length = player.getPlayingTrack().getDuration();
        int minutes = Math.toIntExact((length / 1000) / 60);
        int seconds = Math.toIntExact((length / 1000) - (minutes * 60));
        String duration = minutes + ":" + seconds;

        if(length != 0) {
            channel.sendMessage(author.getAsMention() + " Length of the song is: `" + duration + " minutes`.").queue();
        } else {
            channel.sendMessage(author.getAsMention() + " There is no song playing.").queue();
        }
    }


    public static void setPosition(MessageReceivedEvent e, String servername, String position) {
        MessageChannel channel = e.getChannel();
        User author = e.getAuthor();
        String[] pos = position.split(":");

        int hours = Integer.parseInt(pos[0]) * 60 * 60;
        int minutes = Integer.parseInt(pos[1]) * 60;
        int seconds = Integer.parseInt(pos[2]);

        long finalTime = (hours + minutes + seconds) * 1000;

        TrackScheduler trackScheduler = Main.schedulers.get(servername);
        AudioPlayer player = trackScheduler.getPlayer();
        long length = player.getPlayingTrack().getDuration();

        if(finalTime > length) {
            channel.sendMessage(author.getAsMention() + " I can't do that since the song isn't that long. `Length: " + (length/1000)/60 + " minutes`").queue();
        } else {
            player.getPlayingTrack().setPosition(finalTime);
        }


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




    public static List<String> getParameters(String[] message) {
        List<String> parameters = new ArrayList<>();
        for (String aMessage : message) {
            if (aMessage.startsWith("--")) {
                parameters.add(aMessage);
            }
        }
        return parameters;
    }


    public static String removeParamUrlEncoded(String[] message) {
        StringBuilder sb = new StringBuilder();
        for (String aMessage : message) {
            if (aMessage.startsWith("--") || aMessage.startsWith(">")) {
                continue;
            }
            sb.append(aMessage + " ");
        }
        return sb.toString();
    }


    public static String queryGoogle(String encodedQuery) {
        /*
           Get the first page of the returned
           google search results and retrieve all
           links from it. Written on 26/03/2017
        */
        try {
            final Elements els = Jsoup.connect(GOOGLE_URL + encodedQuery)
                    .userAgent(USER_AGENT)
                    .get()
                    .select("h3.r a");
            /*
               This only gets the first result.
                The user should be able to choose how many
                results they want. min/default = 1, max = 10.
                Written on 26/03/2017
            */
            Element el = els.get(0);
            return el.attr("href");
        } catch (IOException ex) {
            ex.printStackTrace();
            return " Something went wrong and I couldn't find what you were looking for.";
        }
    }




    public static String querySoundCloud(String encodedQuery, String type) {
        try {
            final Elements els = Jsoup.connect(SOUNDCLOUD_URL + type + "?q=" + encodedQuery)
                    .userAgent(USER_AGENT)
                    .get()
                    .select("ul");

            Element el = els.get(1).select("a").get(1);
            return el.absUrl("href");
        } catch (IOException ex) {
            ex.printStackTrace();
            return " Something went wrong and I couldn't find what you were looking for.";
        }
    }




    public static void joinVoiceChannel(MessageReceivedEvent e, String channelName, String servername) {
        Guild guild = e.getGuild();
        VoiceChannel vchannel;
        try {
            vchannel = guild.getVoiceChannelsByName(channelName, true).get(0);
        } catch (IndexOutOfBoundsException ex) {
            e.getChannel().sendMessage(e.getAuthor().getAsMention() + " The channel you told me to join doesn't exist.").queue();
            return;
        }
        AudioManager manager = guild.getAudioManager();
        AudioPlayer player = Main.playerManager.createPlayer();
        TrackScheduler trackScheduler = new TrackScheduler(player);

        Main.schedulers.put(servername, trackScheduler);
        player.addListener(trackScheduler);

        manager.setSendingHandler(new AudioPlayerSendHandler(player));
        manager.openAudioConnection(vchannel);
    }




    /*-------------------------------------------
        Utilities
     ------------------------------------------*/



    /**
     * Used to encode the query that will be sent
     * to google as a search query
     * @param s the string to encode
     * @return the encoded string
     */
    public static String encodeString(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return "error";
    }
}
