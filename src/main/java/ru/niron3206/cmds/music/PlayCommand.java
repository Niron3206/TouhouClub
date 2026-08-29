package ru.niron3206.cmds.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.niron3206.Config;
import ru.niron3206.audioplayer.AutoLeave;
import ru.niron3206.audioplayer.PlayerManager;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.Groups;
import ru.niron3206.cmds.ICommand;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

// отдельный случай от MusicCommand
public class PlayCommand implements ICommand {

    private static final Logger LOG = LoggerFactory.getLogger(PlayCommand.class);

    private static final Set<String> AUDIO_EXTENSIONS = Set.of("mp3", "aac", "wav", "flac", "ogg", "m4a");

    @Override
    public void handle(CommandContext ctx) {
        GuildMessageChannel channel = ctx.getChannel();
        List<Message.Attachment> attachments = ctx.getEvent().getMessage().getAttachments();

        if (ctx.getArgs().isEmpty() && attachments.isEmpty()) {
            channel.sendMessage("🔴 Ничего не понял, вот как должно быть: `" + usage() + "`").queue();
            return;
        }

        Member member = ctx.getMember();
        GuildVoiceState memberVoiceState = member == null ? null : member.getVoiceState();

        if (memberVoiceState == null || !memberVoiceState.inAudioChannel()) {
            channel.sendMessage("🔴 Ты должен зайти в голосовой канал!").queue();
            return;
        }

        AudioManager audioManager = ctx.getGuild().getAudioManager();

        if (!audioManager.isConnected()) {
            if (!connect(ctx, channel, memberVoiceState.getChannel())) {
                return;
            }
        }

        String fileName = audioAttachmentName(attachments);

        if (fileName != null) {
            PlayerManager.getInstance()
                    .loadAndPlay(channel, attachments.get(0).getUrl(), member.getEffectiveName(), fileName);
            return;
        }

        String link = String.join(" ", ctx.getArgs());

        if (link.isBlank()) {
            channel.sendMessage("🔴 Этот файл не похож на аудио, а ссылки ты не дал!").queue();
            return;
        }

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance().loadAndPlay(channel, link, member.getEffectiveName(), null);
    }

    private boolean connect(CommandContext ctx, GuildMessageChannel channel, AudioChannelUnion target) {
        Member self = ctx.getGuild().getSelfMember();

        if (!self.hasPermission(target, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
            channel.sendMessageFormat("🔴 У меня нет прав, чтобы зайти в `%s`", target.getName()).queue();
            return false;
        }

        AudioManager audioManager = ctx.getGuild().getAudioManager();

        audioManager.setSendingHandler(PlayerManager.getInstance().getMusicManager(ctx.getGuild()).getHandler());

        channel.sendMessageFormat("🔌 Подключаюсь к `🔊 %s`", target.getName()).queue();
        audioManager.openAudioConnection(target);
        AutoLeave.watch(ctx.getGuild());

        LOG.debug("Подключение к {}: статус {}", target.getId(), audioManager.getConnectionStatus());

        return true;
    }

    private String audioAttachmentName(List<Message.Attachment> attachments) {
        if (attachments.isEmpty()) {
            return null;
        }

        String extension = attachments.get(0).getFileExtension();

        if (extension == null || !AUDIO_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
            return null;
        }

        return attachments.get(0).getFileName();
    }

    private boolean isUrl(String value) {
        try {
            String scheme = new URI(value).getScheme();
            return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
        } catch (URISyntaxException e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Играет песенки, которые вы поставите\nКак использовать: `" + usage() + "`";
    }

    private static String usage() {
        return Config.prefix() + "play <ютуб ссылка, ссылка на аудио или прикреплённый аудиофайл>";
    }

    @Override
    public Groups getGroup() {
        return Groups.MUSIC;
    }

    @Override
    public List<String> getAliases() {
        return List.of("p");
    }
}
