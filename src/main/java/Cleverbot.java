import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

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
        List<User> mentions = e.getMessage().getMentionedUsers();
        User bot = e.getJDA().getSelfUser();


        if(e.isFromType(ChannelType.TEXT) && mentions.contains(bot) && !e.getAuthor().isBot() && mentions.size() == 1) {

            Thread messageThread = new Thread(() -> {
                String[] split = msg.split(" ", 2);
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
                    botMessage = bot1Session.think(split[1]);
                    long endTime = System.currentTimeMillis();

                    long finalTime = endTime - startTime;
                    channel.sendMessage(botMessage + " `" + finalTime + "ms`").queue();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            messageThread.start();
        }
        else if(e.isFromType(ChannelType.PRIVATE) && mentions.contains(bot) && !e.getAuthor().isBot() && mentions.size() == 1) {
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

