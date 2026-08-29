package ru.niron3206.cmds.music;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import ru.niron3206.audioplayer.MusicManager;
import ru.niron3206.audioplayer.PlayerManager;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.Groups;
import ru.niron3206.cmds.ICommand;

// абстрактный класс для музыкальных команд
// дети получают уже готовый контекст и гильдии
public abstract class MusicCommand implements ICommand {

    @Override
    public final void handle(CommandContext ctx) {
        GuildMessageChannel channel = ctx.getChannel();
        AudioChannelUnion selfChannel = selfVoiceChannel(ctx);

        if (selfChannel == null) {
            channel.sendMessage("🔴 Я должен находиться в голосовом канале!").queue();
            return;
        }

        Member member = ctx.getMember();
        GuildVoiceState memberVoiceState = member == null ? null : member.getVoiceState();

        if (memberVoiceState == null || !memberVoiceState.inAudioChannel()) {
            channel.sendMessage("🔴 Ты должен зайти в голосовой канал!").queue();
            return;
        }

        if (!selfChannel.equals(memberVoiceState.getChannel())) {
            channel.sendMessage("🔴 Мы должны быть в одном и том же канале!").queue();
            return;
        }

        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

        if (requiresPlayingTrack() && musicManager.audioPlayer.getPlayingTrack() == null) {
            channel.sendMessage("Никакой музыки не проигрывается...").queue();
            return;
        }

        handleMusic(ctx, musicManager);
    }

    protected abstract void handleMusic(CommandContext ctx, MusicManager musicManager);

    // флаг для играющих треков
    protected boolean requiresPlayingTrack() {
        return false;
    }

    // selfVoiceChannel + проверка на null
    protected static AudioChannelUnion selfVoiceChannel(CommandContext ctx) {
        GuildVoiceState voiceState = ctx.getGuild().getSelfMember().getVoiceState();

        return voiceState == null || !voiceState.inAudioChannel() ? null : voiceState.getChannel();
    }

    @Override
    public Groups getGroup() {
        return Groups.MUSIC;
    }
}
