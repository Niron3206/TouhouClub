package ru.niron3206.audioplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TrackScheduler.class);

    public final AudioPlayer player;
    public final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();

    // меняется из потоков команд, читается из потоков lavaplayer
    public volatile boolean looping = false;

    private volatile GuildMessageChannel announceChannel;
    private volatile String retriedIdentifier;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
    }

    public void setAnnounceChannel(GuildMessageChannel channel) {
        this.announceChannel = channel;
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void nextTrack() {
        retriedIdentifier = null;
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (!endReason.mayStartNext) {
            return;
        }

        if (endReason == AudioTrackEndReason.LOAD_FAILED) {
            if (!track.getIdentifier().equals(retriedIdentifier)) {
                LOG.info("Повторная попытка для {}", track.getIdentifier());
                retriedIdentifier = track.getIdentifier();
                player.startTrack(track.makeClone(), false);
                return;
            }

            announce("🔴 Не смог проиграть `" + track.getInfo().title + "`, пропускаю.");
            nextTrack();
            return;
        }

        // повтор упавшего трека зациклил бы ту же ошибку навсегда
        if (looping) {
            player.startTrack(track.makeClone(), false);
            return;
        }

        nextTrack();
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        LOG.warn("Ошибка воспроизведения {}", track.getIdentifier(), exception);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        LOG.warn("Трек {} завис более чем на {} мс", track.getIdentifier(), thresholdMs);

        announce("🔴 Трек `" + track.getInfo().title + "` завис, пропускаю.");
        nextTrack();
    }

    private void announce(String message) {
        GuildMessageChannel channel = announceChannel;

        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }
}
