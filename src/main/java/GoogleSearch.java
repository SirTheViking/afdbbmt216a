import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class GoogleSearch extends Command {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        String[] message = e.getMessage().getContent().split(" ", 2);


    }

}
