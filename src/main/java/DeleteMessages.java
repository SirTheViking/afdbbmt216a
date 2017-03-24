import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DeleteMessages extends Command {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        respondToMessage(e);
    }

    private void respondToMessage(MessageReceivedEvent e) {
        /*
            You should check if the message is coming from a server
            or from a private chat before this.
            20/03/2017 it works for now because its only in one server.
         */
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

        String[] messages = e.getMessage().getContent().split(" ");
        if(setAliases(Main.prefixes.get(servername), "rm", "delete").contains(messages[0])) {
            Database.incrementCommand("deleteMessages");
            Thread deleteThread = new Thread(() -> {

                int toDelete = 10;
                TextChannel channel = e.getTextChannel();
                MessageChannel send = e.getChannel();

                if(messages.length > 1) {
                    try {
                        toDelete = Integer.parseInt(messages[1]);
                    } catch (NumberFormatException ex) {
                        // Do something here, or not
                    }
                }
            /*
                In order to make this work or NOT work properly
                everywhere you should check if the bot has permission
                to delete messages in the channel. Written on 20/03/2017
             */
            /*
                This is where we check if the bot has permission
                to delete messages in the selected text channel.
                Written on 20/03/2017
             */
                if(e.isFromType(ChannelType.TEXT)) {
                    if(e.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                        channel.getHistory().retrievePast(toDelete).queue(history -> {
                            channel.deleteMessages(history).queue();
                        });
                    } else {
                        send.sendMessage(e.getAuthor().getAsMention() + ", I can't delete messages without admin.").queue();
                    }
                } else {
                /*
                    Since you cant delete everybodies messages in private chats
                    and the bot can't join DM groups YET this will be sent if you
                    try to do it. Written on 20/03/2017
                 */
                    send.sendMessage(e.getAuthor().getAsMention() + ", I can't delete your messages in here and you can't delete mine.").queue();
                }
            });
            deleteThread.start();

        }
        else if(setAliases(Main.prefixes.get(servername), "nuke").contains(messages[0])) {
            Database.incrementCommand("nuke");

            Thread deleteThread = new Thread(() -> {
                int toDelete = 100;
                TextChannel channel = e.getTextChannel();
                MessageChannel send = e.getChannel();
            /*
                In order to make this work or NOT work properly
                everywhere you should check if the bot has permission
                to delete messages in the channel. Written on 20/03/2017
             */
            /*
                This is where we check if the bot has permission
                to delete messages in the selected text channel.
                Written on 20/03/2017
             */
                if (e.isFromType(ChannelType.TEXT)) {
                    if (e.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                        channel.getHistory().retrievePast(toDelete).queue(history -> {
                            channel.deleteMessages(history).queue();
                        });
                    } else {
                        send.sendMessage(e.getAuthor().getAsMention() + ", I can't delete messages without admin.").queue();
                    }
                } else {
                /*
                    Since you cant delete everybodies messages in private chats
                    and the bot can't join DM groups YET this will be sent if you
                    try to do it. Written on 20/03/2017
                 */
                    send.sendMessage(e.getAuthor().getAsMention() + ", I can't delete your messages in here and you can't delete mine.").queue();
                }
            });
            deleteThread.start();
        }
    }

}
