package ru.niron3206.cmds.music;

import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import ru.niron3206.audioplayer.AutoLeave;
import ru.niron3206.audioplayer.MusicManager;
import ru.niron3206.cmds.CommandContext;

public class LeaveCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, MusicManager musicManager) {
        AudioChannelUnion voiceChannel = selfVoiceChannel(ctx);

        musicManager.scheduler.looping = false;
        musicManager.scheduler.queue.clear();
        musicManager.audioPlayer.stopTrack();

        ctx.getGuild().getAudioManager().closeAudioConnection();
        AutoLeave.cancel(ctx.getGuild());

        ctx.getChannel()
                .sendMessageFormat("Выхожу из `🔊 %s`", voiceChannel == null ? "канала" : voiceChannel.getName())
                .queue();
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getHelp() {
        return "Бот выходит из голосового чата";
    }
}
