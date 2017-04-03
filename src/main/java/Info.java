import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class Info extends Command {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String[] message = e.getMessage().getContent().split(" ");

        if(setAliases(">", "serverinfo").contains(message[0])) {
            MessageChannel channel = e.getChannel();

            if(e.isFromType(ChannelType.TEXT)) {
                EmbedBuilder sEb = serverInfo(e);
                channel.sendMessage(sEb.build()).queue();
            }
        } else if (setAliases(">", "userinfo").contains(message[0])) {
            MessageChannel channel = e.getChannel();

            if(e.isFromType(ChannelType.TEXT)) {
                if(message.length == 2) {
                    User target = e.getGuild().getMembersByEffectiveName(message[1], false).get(0).getUser();
                    EmbedBuilder eb = userInfo(e, target);
                    channel.sendMessage(eb.build()).queue();
                } else {
                    channel.sendMessage(e.getAuthor().getAsMention() + " You need to provide a username.").queue();
                }
            }
        }
    }



    private EmbedBuilder userInfo(MessageReceivedEvent e, User target) {
        EmbedBuilder eb = new EmbedBuilder();
        JDA jda = e.getJDA();

        eb.setAuthor(jda.getSelfUser().getName(), "http://artstation.com", jda.getSelfUser().getAvatarUrl());
        eb.setThumbnail(target.getAvatarUrl());
        eb.addField("Name: ", target.getName(), false);
        eb.addField("Avatar-URL: ", "[`cdn.discordapp.com`](" + target.getAvatarUrl() + ")", false);
        eb.addField("Created: ", target.getCreationTime().toString().split("T")[0], false);
        eb.setColor(new Color(242, 242, 242));

        return eb;
    }




    private EmbedBuilder serverInfo(MessageReceivedEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        JDA jda = e.getJDA();
        Guild guild = e.getGuild();

        eb.setThumbnail(guild.getIconUrl());
        eb.setAuthor(jda.getSelfUser().getName(), "http://artstation.com", jda.getSelfUser().getAvatarUrl());
        eb.addField("Name: ", guild.getName(), false);
        eb.addField("Owner: ", guild.getOwner().getAsMention(), false);
        eb.addField("Icon-URL: ", "[`cdn.discordapp.com`](" + guild.getIconUrl() + ")", false);
        eb.addField("Users: ", guild.getMembers().size() + "", false);
        eb.addField("Region: ", guild.getRegion().toString(), false);
        eb.setColor(new Color(242, 242, 242));
        return eb;
    }

}
