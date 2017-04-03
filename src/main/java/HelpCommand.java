import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
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
            String[] message = e.getMessage().getContent().split(" ");

            if(aliases.contains(message[0])) {
                List<String> parameters = Methods.getParameters(message);
                MessageChannel channel = e.getChannel();
                User author = e.getAuthor();
                PrivateChannel privateChannel = e.getAuthor().openPrivateChannel().complete();

                if(parameters.size() > 0) {
                    for(String param : parameters) {
                        switch (param) {
                            case "--info":
                                EmbedBuilder eb = usageEmbed(e);
                                channel.sendMessage(eb.build()).queue();
                                break;

                            case "--problems":
                                EmbedBuilder bEb = bugsEmbed(e);
                                privateChannel.sendMessage(bEb.build()).queue();
                                channel.sendMessage(author.getAsMention() + " I've sent you the list of problems.").queue();
                                break;

                            case "--param":
                                EmbedBuilder pEb = paramEmbed(e);
                                privateChannel.sendMessage(pEb.build()).queue();
                                channel.sendMessage(author.getAsMention() + " I've sent you the list of parameters.").queue();
                                break;
                        }
                    }
                } else if(parameters.size() == 0) {
                    EmbedBuilder eb = helpEmbed(e);
                    privateChannel.sendMessage(eb.build()).queue();
                    channel.sendMessage(author.getAsMention() + " I've sent the command list to you.").queue();
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
                        "\n\n_**NEW**_ >help --param   :   will bring up the help menu for command parameters" +
                        "\n\n>g  <link>   :   google for anything and get the first result" +
                        "\n\n>gets  <twitch channel name>   :   will return a link the the stream and wether or not the stream is live" +
                        "\n\n_**NEW**_>serverinfo   :   will return relevant info about the server" +
                        "\n\n_**NEW**_>userinfo <username>   :   will return info about the user with the provided username" +
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
        eb.addBlankField(true);
        eb.setFooter("Made by: Rip#9604  -  Feel free to contact with feedback or ask for help", null);
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
        //long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        eb.setAuthor(jda.getSelfUser().getName(), "https://www.artstation.com", jda.getSelfUser().getAvatarUrl());
        //eb.addField("Mem Usage: ", (usedMemory/1000000) + "MB", true);
        eb.addField("Users: ", Integer.toString(jda.getUsers().size()), true);
        eb.addField("Guilds: ", Integer.toString(jda.getGuilds().size()), true);
        eb.addField("Ping: ", jda.getPing() + "ms", true);
        eb.addField("Author: ", "Rip#9604", true);
        eb.addField("Library: ", "JDA", true);
        eb.addField("GitHub: ", "soon™", true);
        eb.setColor(new Color(242, 242, 242));

        return eb;
    }




    private static EmbedBuilder bugsEmbed(MessageReceivedEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        JDA jda = e.getJDA();

        eb.setAuthor(jda.getSelfUser().getName(), "http://artstation.com", jda.getSelfUser().getAvatarUrl());
        eb.addField("Problems: ", "\nThe discord voice engine has a bug that affects the music player.\n" +
                "**Explanation:** If the bot is in 2 voice channels at the same time, let's say, playing a song" +
                "if it were to leave one of them, the other server would get the bot to stop playing til you reconnect.", false);
        eb.addBlankField(true);
        eb.setFooter("Made by: Rip#9604  -  Feel free to contact with feedback or ask for help", null);
        eb.setColor(new Color(242, 242, 242));

        return eb;
    }




    private static EmbedBuilder paramEmbed(MessageReceivedEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        JDA jda = e.getJDA();

        eb.setAuthor(jda.getSelfUser().getName(), "http://artstation.com", jda.getSelfUser().getAvatarUrl());
        eb.addField("Parameters: ", "These are parameters that can be used in combination with commands:\n" +
                "**Example:** _>q --g you want a piece of me song --join:general_ -> will google for the song and" +
                "queue it in the voice channel general." +
                "\n\n**>help**   :   \n--param (brings up this menu), \n--info (brings up info about the bot), \n--problems (brings up problems about the bot)" +
                "\n\n**>g**   :   \n--q (queues the google result)" +
                "\n\n**>q**   :   \n--sc (searches soundcloud), \n--s (searches sc song, this or --ps must be used), \n--ps (searches sc playlist, this or --s must be used), \n--g (first google result), \n--join:<voice channel name>" +
                "\n\n**>gets**  :   TODO", true);
        eb.addBlankField(true);
        eb.setFooter("Made by: Rip#9604  -  Feel free to contact with feedback or ask for help", null);
        eb.setColor(new Color(242, 242, 242));

        return eb;
    }

}
