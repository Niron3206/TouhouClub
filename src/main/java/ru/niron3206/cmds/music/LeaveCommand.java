package ru.niron3206.cmds.music;

import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import ru.niron3206.audioplayer.AutoLeave;
import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.cmds.CommandContext;

public class LeaveCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        AudioChannelUnion voiceChannel = selfVoiceChannel(ctx);

        AutoLeave.leave(ctx.getGuild());

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
