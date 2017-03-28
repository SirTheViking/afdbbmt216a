import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Cleverbot extends Command {

    private ChatterBotFactory factory = new ChatterBotFactory();

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

        String msg = e.getMessage().getContent();

        if(e.isFromType(ChannelType.TEXT) && (msg.indexOf("-") == 0) && !e.getAuthor().isBot()) {
            /*
            As every command class will eventually contain this
            It checks whether or not the HashMap already contains
            the server that the message is being sent from. If not
            then it adds it. Written on 19/03/2017
         */
            String servername = e.getGuild().getName(); //This is temporary
            if(!Database.checkForServer(servername)) {
                Main.prefixes.put(servername, ">");
                Database.writeToPrefixes(servername);
            }

            Thread messageThread = new Thread(() -> {
                MessageChannel channel = e.getChannel();
                try {
                    ChatterBot bot1 = factory.create(ChatterBotType.CLEVERBOT, Main.cleverAPI);
                    ChatterBotSession bot1Session = bot1.createSession();

                    String botMessage;

                    /*
                        The time variables are to see how good/bad
                        the response time is. Written on 19/03/2017
                    */
                    long startTime = System.currentTimeMillis();
                    botMessage = bot1Session.think(msg);
                    long endTime = System.currentTimeMillis();

                    long finalTime = endTime - startTime;
                    channel.sendMessage(botMessage + " `" + finalTime + "ms`").queue();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            messageThread.start();
        }
        else if(e.isFromType(ChannelType.PRIVATE) && !e.getAuthor().isBot()) {
            Thread messageThread = new Thread(() -> {
                MessageChannel channel = e.getChannel();
                try {
                    ChatterBot bot1 = factory.create(ChatterBotType.CLEVERBOT, Main.cleverAPI);
                    ChatterBotSession bot1Session = bot1.createSession();

                    String botMessage;

                    /*
                        The time variables are to see how good/bad
                        the response time is. Written on 19/03/2017
                    */
                    long startTime = System.currentTimeMillis();
                    botMessage = bot1Session.think(msg);
                    long endTime = System.currentTimeMillis();

                    long finalTime = endTime - startTime;
                    channel.sendMessage(botMessage + " `" + finalTime + "ms`").queue();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            messageThread.start();
        }

    }

}

