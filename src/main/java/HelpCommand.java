import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class HelpCommand extends Command {

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
        String message = e.getMessage().getContent();

        String servername = "PRIVATE"; //This is temporary
        if(e.isFromType(ChannelType.TEXT)) {
            servername = e.getGuild().getName();
        }

        /*
            As every command class will eventually contain this
            It checks whether or not the HashMap already contains
            the server that the message is being sent from. If not
            then it adds it. Written on 20/03/2017
         */
        if(!Database.checkForServer(servername) && (!servername.equals("PRIVATE"))) {
            Main.prefixes.put(servername, ">");
            Database.writeToPrefixes(servername);
        }

        if(setAliases(Main.prefixes.get(servername), "help", "commands").contains(message)) {
            Thread messageThread = new Thread(() -> {
                MessageChannel channel = e.getChannel();
                EmbedBuilder eb = helpEmbed(e);
                channel.sendMessage(eb.build()).queue();
            });
            messageThread.start();
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
        long usedMemory  = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        eb.setAuthor(jda.getSelfUser().getName(), "https://www.artstation.com/", jda.getSelfUser().getAvatarUrl());
        eb.addBlankField(true);
        eb.addField("Commands: ", ">roll : rolls a number between 1 and x (x = 10 by default)" +
            "\n\nadding a dash '-' to a message will make the bot respond to it" +
            "\n\n>help : will bring up this menu" +
            "\n\neastereggs exist, see if you can find them", false);
        eb.addBlankField(false);
        eb.addField("", "Mem usage: " + (usedMemory/1000000) + "MB", true);
        eb.addField("", "Ping: " + (jda.getPing()) + "ms", false);
        eb.setColor(new Color(242, 242, 242));

        return eb;
    }

}
