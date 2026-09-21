package ru.niron3206.cmds.music;

import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import ru.niron3206.Config;
import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.cmds.CommandContext;

import java.util.ArrayList;
import java.util.List;

public class RemoveCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        GuildMessageChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            channel.sendMessage("🔴 Не понял, какой трек убрать: `" + Config.prefix() + "remove <номер трека в очереди>`").queue();
            return;
        }

        List<Track> tracks = new ArrayList<>(music.queue);

        if (tracks.isEmpty()) {
            channel.sendMessage("Очередь пуста").queue();
            return;
        }

        int position;

        try {
            position = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            channel.sendMessage("🔴 Номер трека должен быть числом, а не `" + args.get(0) + "`!").queue();
            return;
        }

        if (position < 1 || position > tracks.size()) {
            channel.sendMessageFormat("🔴 В очереди `%d` треков, номера `%d` там нет!", tracks.size(), position).queue();
            return;
        }

        Track track = tracks.get(position - 1);

        music.queue.remove(track);

        channel.sendMessage("🎵 Трек `" + track.getInfo().getTitle() + "` был удалён из очереди!").queue();
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getHelp() {
        return "Удаляет выбранный трек из очереди\nКак использовать: `" + Config.prefix() + "remove <номер трека в очереди>`";
    }

    @Override
    public List<String> getAliases() {
        return List.of("r");
    }
}
