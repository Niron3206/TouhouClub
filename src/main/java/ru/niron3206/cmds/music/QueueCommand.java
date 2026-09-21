package ru.niron3206.cmds.music;

import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import net.dv8tion.jda.api.entities.Message;
import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.util.TimeFormat;

import java.util.ArrayList;
import java.util.List;

public class QueueCommand extends MusicCommand {

    private static final int MAX_TRACKS = 20;
    private static final int MAX_TITLE_LENGTH = 80;
    // место под хвост N более...
    private static final int TAIL_RESERVE = 40;

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        List<Track> tracks = new ArrayList<>(music.queue);

        if (tracks.isEmpty()) {
            ctx.getChannel().sendMessage("Очередь пуста").queue();
            return;
        }

        ctx.getChannel().sendMessage(buildText(tracks)).queue();
    }

    static String buildText(List<Track> tracks) {
        StringBuilder text = new StringBuilder("📃 **Следующие треки:**\n");
        int shown = 0;

        while (shown < tracks.size() && shown < MAX_TRACKS) {
            String entry = format(shown + 1, tracks.get(shown));

            // 20 треков могут и не влезть
            if (text.length() + entry.length() + TAIL_RESERVE > Message.MAX_CONTENT_LENGTH) {
                break;
            }

            text.append(entry);
            shown++;
        }

        if (shown < tracks.size()) {
            text.append("И `").append(tracks.size() - shown).append("` более...");
        }

        return text.toString();
    }

    private static String format(int position, Track track) {
        TrackInfo info = track.getInfo();

        return "#" + position + " `" + shorten(info.getTitle()) + "`"
                + "\n(Ссылка: " + info.getUri() + ")"
                + "\nАвтор: `" + info.getAuthor() + "` [`"
                + (info.isStream() ? "стрим" : TimeFormat.format(info.getLength())) + "`]\n";
    }

    private static String shorten(String title) {
        return title.length() <= MAX_TITLE_LENGTH ? title : title.substring(0, MAX_TITLE_LENGTH - 1) + "…";
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getHelp() {
        return "Показывает предложенные песни";
    }

    @Override
    public List<String> getAliases() {
        return List.of("q");
    }
}
