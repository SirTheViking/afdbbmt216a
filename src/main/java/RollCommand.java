import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RollCommand extends Command {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        /*
            Im not really sure how the library really works
            maybe it creates a new thread by itself so threads
            might be worthless but we can keep them for now.
            Written on 20/03/2017
         */
        respondToMessage(e);
    }



    /**
     * Creates a new thread, generates a random number and sends it
     * directly through to discord, hence why it does not return
     * anything.
     * @param e The library event containing all of the message information
     */
    private void respondToMessage(MessageReceivedEvent e) {
        /*
            You should check if the message is coming from a server
            or from a private chat before this.
            19/03/2017 it works for now because its only in one server.
         */
        String servername = "PRIVATE"; //This is temporary
        if(e.isFromType(ChannelType.TEXT)) {
            servername = e.getGuild().getName();
        }
        /*
            As every command class will eventually contain this
            It checks whether or not the HashMap already contains
            the server that the message is being sent from. If not
            then it adds it. Written on 19/03/2017
         */
        if(!Database.checkForServer(servername) && (!servername.equals("PRIVATE"))) {
            Main.prefixes.put(servername, ">");
            Database.writeToPrefixes(servername);
        }

        String[] message = e.getMessage().getContent().split(" ");
        if(setAliases(Main.prefixes.get(servername), "roll", "dice").contains(message[0])) {
            /*
                Everything in here runs if the message properly contains
                the prefix + roll or dice as the first word of the sentence
                Written on 19/03/2017
             */
            MessageChannel channel = e.getChannel();
            User author = e.getAuthor();
            /*
                There may be a second argument that specifies the numbers to roll between
                We check for that here. Written on 19/03/2017
             */
            int max = 10;
            if(message.length > 1) {
                max = Integer.parseInt(message[1]);
            }
            /*
                This will have to be properly done later, checking
                having an answer from wherever the message is coming
                not just TEXT channels. Written on 19/03/2017
             */
            if(e.isFromType(ChannelType.TEXT)) {
                int result = randomNr(max);
                channel.sendMessage(author.getAsMention() + " You rolled " + result + " out of " + max).queue();
            }
            else if(e.isFromType(ChannelType.PRIVATE)) {
                int result = randomNr(max);
                channel.sendMessage(author.getAsMention() + " You rolled " + result + " out of " + max).queue();
            }
        }
    }

}
