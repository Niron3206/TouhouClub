package ru.niron3206.cmds.music;

import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import ru.niron3206.Config;
import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.cmds.CommandContext;

import java.util.List;

public class VolumeCommand extends MusicCommand {

    // у узла шкала та же: 100 = 100%
    private static final int MAX_VOLUME = 200;

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        GuildMessageChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            channel.sendMessageFormat("🔊 Громкость: `%d%%`", player(ctx).getVolume()).queue();
            return;
        }

        int volume = parse(args.get(0));

        if (volume < 0 || volume > MAX_VOLUME) {
            channel.sendMessage("🔴 Громкость: число от 0 до " + MAX_VOLUME + ": `" + usage() + "`").queue();
            return;
        }

        updatePlayer(ctx, update -> update.setVolume(volume), "🔊 Громкость: `" + volume + "%`");
    }

    private static int parse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String usage() {
        return Config.prefix() + "volume <0-" + MAX_VOLUME + ">";
    }

    @Override
    protected boolean requiresPlayingTrack() {
        return true;
    }

    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public String getHelp() {
        return "Меняет громкость, без числа показывает текущую\nКак использовать: `" + usage() + "`";
    }

    @Override
    public List<String> getAliases() {
        return List.of("v");
    }
}
