package ru.niron3206.cmds.music;

import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.audioplayer.LavalinkManager;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.Groups;
import ru.niron3206.cmds.ICommand;

// абстрактный класс для музыкальных команд
// дети получают уже проверенный контекст и очередь гильдии
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

        if (requiresPlayingTrack() && playingTrack(ctx) == null) {
            channel.sendMessage("Никакой музыки не проигрывается...").queue();
            return;
        }

        handleMusic(ctx, LavalinkManager.getInstance().getMusic(ctx.getGuild().getIdLong()));
    }

    protected abstract void handleMusic(CommandContext ctx, GuildMusic music);

    // флаг для команд, которым нужен играющий трекк
    protected boolean requiresPlayingTrack() {
        return false;
    }

    protected static Track playingTrack(CommandContext ctx) {
        return LavalinkManager.getInstance().getPlayingTrack(ctx.getGuild().getIdLong());
    }

    protected static LavalinkPlayer player(CommandContext ctx) {
        return LavalinkManager.getInstance().getPlayer(ctx.getGuild().getIdLong());
    }

    // голосовой канал бота или null, если бот никуда не подключён
    protected static AudioChannelUnion selfVoiceChannel(CommandContext ctx) {
        GuildVoiceState voiceState = ctx.getGuild().getSelfMember().getVoiceState();

        return voiceState == null || !voiceState.inAudioChannel() ? null : voiceState.getChannel();
    }

    @Override
    public Groups getGroup() {
        return Groups.MUSIC;
    }
}
