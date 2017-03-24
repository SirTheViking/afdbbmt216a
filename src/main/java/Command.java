
import net.dv8tion.jda.core.hooks.ListenerAdapter;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Command extends ListenerAdapter {

    /**
     * This is used everytime a message is sent in order to see
     * if it contains a command that can be taken as a proper
     * argument by the listener. Written on 19/03/2017
     * @param prefix The server prefix that the message was sent from
     * @param aliases The possible word combinations that the bot will respond to
     *                with the specified command response
     * @return
     */
    protected List<String> setAliases(String prefix, String... aliases) {
        List<String> words = new ArrayList<>();
        for(int i = 0; i < aliases.length; i++) {
            words.add(prefix + aliases[i]);
        }
        return words;
    }

    /*
        Methods like this will eventually need to be moved
        to a file that only contains this type. It could be
        a file for useful methods that can be used in other
        projects.
     */

    /**
     * Method for generating random numbers, can be useful
     * in a plethora of cases, especially with a bot like
     * this one.
     * @param max The maximum number it should randomise to from 0 (inclusive)
     *            to 10 (exclusive) for example
     * @return returns the randomized number
     */
    public static int randomNr(int max) {
        Random r = new Random();
        int low = 1;
        int high = max + 1;
        int result = r.nextInt(high - low) + low;
        return result;
    }

}
