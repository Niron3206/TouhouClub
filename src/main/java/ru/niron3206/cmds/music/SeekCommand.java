package ru.niron3206.cmds.music;

import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import ru.niron3206.Config;
import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.util.TimeFormat;

import java.util.List;

public class SeekCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        GuildMessageChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            channel.sendMessage("🔴 Не понял, куда перемотать: `" + usage() + "`").queue();
            return;
        }

        TrackInfo info = playingTrack(ctx).getInfo();

        // узел молча игнорирует перемотку таких треков
        if (info.isStream() || !info.isSeekable()) {
            channel.sendMessage("🔴 Этот трек нельзя перематывать!").queue();
            return;
        }

        long position = TimeFormat.parse(args.get(0));

        if (position < 0) {
            channel.sendMessage("🔴 Не понял время `" + args.get(0) + "`: `" + usage() + "`").queue();
            return;
        }

        if (position >= info.getLength()) {
            channel.sendMessage("🔴 Трек длится всего `" + TimeFormat.format(info.getLength()) + "`!").queue();
            return;
        }

        String reply = position == 0
                ? "⏮ Перематываю в начало"
                : "⏩ Перематываю на `" + TimeFormat.format(position) + "`";

        updatePlayer(ctx, update -> update.setPosition(position), reply);
    }

    private static String usage() {
        return Config.prefix() + "seek <мм:сс>";
    }

    @Override
    protected boolean requiresPlayingTrack() {
        return true;
    }

    @Override
    public String getName() {
        return "seek";
    }

    @Override
    public String getHelp() {
        return "Перематывает трек на указанное время\nКак использовать: `" + usage() + "`";
    }
}
