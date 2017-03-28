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


        if(e.isFromType(ChannelType.TEXT)) {
            String servername = e.getGuild().getName();
            String[] messages = e.getMessage().getContent().split(" ");

            if(setAliases(Main.prefixes.get(servername), "rm", "delete").contains(messages[0])) {

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
                    if(e.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                        channel.getHistory().retrievePast(toDelete).queue(history -> channel.deleteMessages(history).queue());
                    } else {
                        send.sendMessage(e.getAuthor().getAsMention() + ", I can't delete messages without admin.").queue();
                    }
                });
                deleteThread.start();
            } else if(setAliases(Main.prefixes.get(servername), "nuke").contains(messages[0])) {

                Thread deleteThread = new Thread(() -> {
                    int toDelete = 100;
                    TextChannel channel = e.getTextChannel();
                    MessageChannel send = e.getChannel();
                    /*
                        In order to make this work or NOT work properly
                        everywhere you should check if the bot has permission
                        to delete messages in the channel. Written on 20/03/2017
                    */
                    if (e.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                        channel.getHistory().retrievePast(toDelete).queue(history -> channel.deleteMessages(history).queue());
                    } else {
                        send.sendMessage(e.getAuthor().getAsMention() + ", I can't delete messages without admin.").queue();
                    }
                });
                deleteThread.start();
            }
        } else if (e.isFromType(ChannelType.PRIVATE)) {
            String[] messages = e.getMessage().getContent().split(" ");

            if(setAliases(">", "rm", "delete").contains(messages[0])) {
                e.getChannel().sendMessage("That command does not work here yet.").queue();
            } else if (setAliases(">", "nuke").contains(messages[0])) {
                e.getChannel().sendMessage("That command does not work here yet.").queue();
            }
        }

    }

}
