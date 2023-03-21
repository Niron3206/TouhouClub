package ru.niron3206.cmds.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import ru.niron3206.audioplayer.MusicManager;
import ru.niron3206.audioplayer.PlayerManager;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.ICommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getGuildChannel().asTextChannel();
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        if (queue.isEmpty()) {
            channel.sendMessage("Очередь пуста").queue();
            return;
        }

        int trackCount = Math.min(queue.size(), 20);
        List<AudioTrack> trackList = new ArrayList<>(queue);
        MessageCreateAction messageAction = channel.sendMessage("**Очередь:**\n");

        for (int i = 0; i < trackCount; i++) {
            AudioTrack track = trackList.get(i);
            AudioTrackInfo info = track.getInfo();

            messageAction.addContent("#")
                    .addContent(String.valueOf(i + 1))
                    .addContent(" `")
                    .addContent(String.valueOf(info.title))
                    .addContent("`\nАвтор: `")
                    .addContent(info.author)
                    .addContent("` [`")
                    .addContent(formatTime(track.getDuration()))
                    .addContent("`]\n");
        }

        if (trackList.size() > trackCount) {
            messageAction.addContent("И `")
                    .addContent(String.valueOf(trackList.size() - trackCount))
                    .addContent("` более...");
        }

        messageAction.queue();
    }

    private String formatTime(long timeInMillis) {
        long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
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
        return List.of("q", "Q");
    }
}