package ru.niron3206.cmds.music;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import ru.niron3206.audioplayer.AutoLeave;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.Groups;
import ru.niron3206.cmds.ICommand;
import ru.niron3206.audioplayer.PlayerManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class PlayCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getChannel().asTextChannel();
        List<Message.Attachment> attachments = ctx.getEvent().getMessage().getAttachments();

        if (ctx.getArgs() == null && attachments.size() == 0) {
            channel.sendMessage("\uD83D\uDD34 Ничего не понял, вот как должно быть: `~play <ютуб ссылка, ссылка на аудио или прикреплённый аудиофайл>`").queue();
            return;
        }

        Member member = ctx.getEvent().getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()) {
            channel.sendMessage("\uD83D\uDD34 Ты должен зайти в голосовой канал!").queue();
            return;
        }

        Member self = ctx.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            VoiceChannel memberChannel = memberVoiceState.getChannel().asVoiceChannel();
            AudioManager audioManager = ctx.getGuild().getAudioManager();
            channel.sendMessageFormat("\uD83D\uDD0C Подключаюсь к `\uD83D\uDD0A %s`", memberChannel.getName()).queue();
            audioManager.openAudioConnection(memberChannel);
            new AutoLeave(ctx.getGuild()).timer();
        }

        String link = String.join(" ", ctx.getArgs());

        if(!attachments.isEmpty()
                && Arrays.asList("mp3", "aac", "wav", "flac", "ogg", "m4a").contains(attachments.get(0).getFileExtension())) {
            link = attachments.get(0).getUrl();

            PlayerManager.getInstance()
                    .loadAndPlay(channel, link, member.getEffectiveName(), Optional.of(attachments.get(0).getFileName()));
            return;
        }

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance()
                .loadAndPlay(channel, link, member.getEffectiveName(), Optional.empty());
    }

    @Override
    public String getName () {
        return "play";
    }

    @Override
    public String getHelp () {
        return "Играет песенки, которые вы поставите\n" +
                "Как использовать: `~play <ютуб ссылка, ссылка на аудио или прикреплённый аудиофайл>`";
    }

    @Override
    public Groups getGroup() {
        return Groups.MUSIC;
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
