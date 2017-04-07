import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;


public class MusicPlayer extends Command {

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
            Guild guild = e.getGuild();
            String servername = e.getGuild().getName();

            String[] message = e.getMessage().getContent().split(" ");
            switch (message[0]) {

                case ">join":
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
                    Methods.joinVoiceChannel(e, channelName, servername);
                    break;

                case ">q":
                    /*
                    Queues a song from a link, or a playlist
                    Written on 26/03/2017
                    */
                    List<String> parameters = Methods.getParameters(message);
                    String song = "https://www.youtube.com/playlist?list=PL9GDpEaemvz7crT3RNl-ffvuoIC1-KpAi";
                    String vChannel = "general";
                    if(parameters.size() > 0) {
                        for(String param : parameters) {
                            if(param.startsWith("--join") && param.length() > 6) {
                                vChannel = param.split(":")[1];
                                param = "--join";
                            }
                            switch(param) {
                                case "--g":
                                    String gQuery = Methods.removeParamUrlEncoded(message);
                                    String gEncodedQuery = Methods.encodeString(gQuery);
                                    song = Methods.queryGoogle(gEncodedQuery);
                                    break;

                                case "--join":
                                    Methods.joinVoiceChannel(e, vChannel, servername);
                                    break;

                                case "--sc":
                                    String sQuery = Methods.removeParamUrlEncoded(message);
                                    String sEncodedQuery = Methods.encodeString(sQuery);
                                    String type = "";
                                    for(String par : parameters) {
                                        switch (par) {
                                            case "--s":
                                                type = "sounds";
                                                break;

                                            case "--ps":
                                                type = "sets";
                                                break;
                                        }
                                    }
                                    song = Methods.querySoundCloud(sEncodedQuery, type);
                                    break;
                            }
                        }
                    } else if (message.length == 2 && parameters.size() == 0) {
                        song = message[1];
                    }
                    Methods.queueTrack(e, song, servername);
                    break;

                case ">next":
                    /*
                    Move forward into the playlist and
                    play the next song
                    Written on 26/03/2017
                    */
                    Methods.getNext(servername);
                    break;

                case ">pause":
                    /*
                    Pause the player.
                    Written on 26/03/2017
                    */
                    Methods.pausePlayer(e, servername);
                    break;

                case ">play":
                    /*
                    Resume the player.
                    Written on 26/03/2017
                    */
                    Methods.resumePlayer(e, servername);
                    break;

                case ">np":
                    /*
                    Get the song that's playing.
                    Written on 26/03/2017
                    */
                    Methods.playingTrack(e, servername);
                    break;

                case ">leave":
                    /*
                    Leave the channel that the bot is currently in.
                    Written on 26/03/2017
                    */
                    Methods.leaveVoice(guild);
                    break;

                case ">setpos":
                    Methods.setPosition(e, servername, message[1]);
                    break;

                case ">duration":
                    Methods.getDuration(e, servername);
                    break;
            }
        }


    }

}
