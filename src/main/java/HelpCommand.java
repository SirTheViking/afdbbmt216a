import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class HelpCommand extends Command {

    private final List<String> aliases = setAliases(">", "help", "commands");

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        respondToMessage(e);

    }

    /**
     * Creates a new thread and processes the message that was sent
     * by the user. doesn't return anything because it straight
     * up sends the message through to discord.
     * @param e The library event containing all of the message information
     */
    private void respondToMessage(MessageReceivedEvent e) {
        if(e.isFromType(ChannelType.TEXT)) {
            /*
            This checks whether or not the HashMap already contains
            the server that the message is being sent from. If not
            then it adds it. Written on 20/03/2017
         */
            String servername = e.getGuild().getName();
            List<String> message = Arrays.asList(e.getMessage().getContent().split(" "));

            if(aliases.contains(message.get(0))) {
                List<String> parameters = Methods.getParameters(message);

                if(parameters.size() > 0) {
                    for(String param : parameters) {
                        switch (param) {
                            case "--status":
                                MessageChannel channel = e.getChannel();
                                EmbedBuilder eb = usageEmbed(e);
                                channel.sendMessage(eb.build()).queue();
                                break;
                        }
                    }
                } else if(parameters.size() == 0) {
                    MessageChannel channel = e.getChannel();
                    EmbedBuilder eb = helpEmbed(e);
                    channel.sendMessage(eb.build()).queue();
                }
            }
        } else if (e.isFromType(ChannelType.PRIVATE)) {
            String message = e.getMessage().getContent();

            if(aliases.contains(message)) {
                MessageChannel channel = e.getChannel();
                EmbedBuilder eb = helpEmbed(e);
                channel.sendMessage(eb.build()).queue();
            }
        }
    }


    /**
     * Creates the embed to be sent as a response to the message
     * It's an embed because they look pretty and they're very
     * customisable
     * @param e The library even containing all of the message information
     * @return returns the embed to be used
     */
    private EmbedBuilder helpEmbed(MessageReceivedEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        JDA jda = e.getJDA();

        eb.setAuthor(jda.getSelfUser().getName(), "https://www.artstation.com/", jda.getSelfUser().getAvatarUrl());
        eb.addBlankField(true);
        eb.addField("Commands: ", ">roll   :   rolls a number between 1 and x (x = 10 by default)" +
                        "\n\n>help   :   will bring up this menu" +
                        "\n\n>g  <link>   :   google for anything and get the first result" +
                        "\n\n>gets  <twitch channel name>   :   will return a link the the stream and wether or not the stream is live" +
                        "\n\n\nadding a dash '-' to a message will make the bot respond to it", false);
        eb.addBlankField(true);
        eb.addField("Music Commands: ",
                "\n\n>join <channel name>   :   to make the bot join a voice channel" +
                        "\n\n>q <song or playlist>   :   if no song or playlist is provided a random one will be played" +
                        "\n\n>next   :   Move to the next song in the playlist" +
                        "\n\n>pause   :   pause the music" +
                        "\n\n>play   :   resume the music" +
                        "\n\n>np   :   show the name of the song that's playing" +
                        "\n\n>leave   :   leave the voice channel", false);
        eb.setColor(new Color(242, 242, 242));

        return eb;
    }


    /**
     * Used to get information about the bot
     * @param e The event containing all information about the server
     * @return the embed to send back
     */
    private EmbedBuilder usageEmbed(MessageReceivedEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        JDA jda = e.getJDA();
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        eb.setAuthor(jda.getSelfUser().getName(), "https://www.artstation.com", jda.getSelfUser().getAvatarUrl());
        eb.addField("Mem Usage: ", (usedMemory/1000000) + "MB", true);
        eb.addField("Users: ", Integer.toString(jda.getUsers().size()), true);
        eb.addField("Guilds: ", Integer.toString(jda.getGuilds().size()), true);
        eb.addField("Channels: ", Integer.toString(jda.getTextChannels().size()), true);
        eb.addField("Ping: ", jda.getPing() + "ms", true);
        eb.addBlankField(true);
        eb.setColor(new Color(242, 242, 242));

        return eb;
    }

}
