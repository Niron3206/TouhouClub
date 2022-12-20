package ru.niron3206.cmds.music;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.ICommand;
import ru.niron3206.audioplayer.PlayerManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class PlayCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getTextChannel();

        if (ctx.getArgs() == null) {
            channel.sendMessage("Ничего не понял, вот как должно быть: `~play <ютуб ссылка>`").queue();
            return;
        }

        Member self = ctx.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        Member member = ctx.getEvent().getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            channel.sendMessage("Ты должен зайти в голосовой канал, чтобы включить свою классную музычку!").queue();
            return;
        }

        if (!selfVoiceState.inAudioChannel()) {
            AudioChannel memberChannel = memberVoiceState.getChannel();
            AudioManager audioManager = ctx.getGuild().getAudioManager();
            channel.sendMessageFormat("Подключаюсь к `\uD83D\uDD0A %s`", memberChannel.getName()).queue();
            audioManager.openAudioConnection(memberChannel);
        }

        String link = String.join(" ", ctx.getArgs());

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance()
                .loadAndPlay(channel, link);
    }

    @Override
    public String getName () {
        return "play";
    }

    @Override
    public String getHelp () {
        return "Играет песенки, которые вы поставите\n" +
                "Как использовать: `~play <ютуб ссылка>`";
    }

    @Override
    public List<String> getAliases () {
        return List.of("p", "P");
    }

    private boolean isUrl (String url){
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
