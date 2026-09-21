package ru.niron3206.audioplayer;

import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// очередь и состояние одной гильдии
public class GuildMusic {

    public final BlockingQueue<Track> queue = new LinkedBlockingQueue<>();

    // меняется из потоков команд, читается из потока событий Lavalink
    public volatile boolean looping = false;

    private volatile GuildMessageChannel announceChannel;
    private volatile String retriedIdentifier;

    public void setAnnounceChannel(GuildMessageChannel channel) {
        this.announceChannel = channel;
    }

    public void announce(String message) {
        GuildMessageChannel channel = announceChannel;

        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }

    // трек повторяем не больше одного раза, иначе ошибка крутится вечно
    public boolean markRetried(String identifier) {
        if (identifier.equals(retriedIdentifier)) {
            return false;
        }

        retriedIdentifier = identifier;
        return true;
    }

    public void clearRetried() {
        retriedIdentifier = null;
    }
}
