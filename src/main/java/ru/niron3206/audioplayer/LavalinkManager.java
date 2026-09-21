package ru.niron3206.audioplayer;

import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.event.TrackEndEvent;
import dev.arbjerg.lavalink.client.event.TrackExceptionEvent;
import dev.arbjerg.lavalink.client.event.TrackStartEvent;
import dev.arbjerg.lavalink.client.event.TrackStuckEvent;
import dev.arbjerg.lavalink.client.event.WebSocketClosedEvent;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import dev.arbjerg.lavalink.client.player.LoadFailed;
import dev.arbjerg.lavalink.client.player.NoMatches;
import dev.arbjerg.lavalink.client.player.PlaylistLoaded;
import dev.arbjerg.lavalink.client.player.SearchResult;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.client.player.TrackLoaded;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason;
import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.niron3206.Config;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * воспроизведение от Lavalink
 * здесь только очередь, события и разбор результатов загрузки
 */
public class LavalinkManager {

    private static final Logger LOG = LoggerFactory.getLogger(LavalinkManager.class);

    private static final String DEFAULT_URL = "ws://lavalink:2333";
    private static final String DEFAULT_PASSWORD = "youshallnotpass";

    private static volatile LavalinkManager instance;

    private final LavalinkClient client;
    private final Map<Long, GuildMusic> guilds = new ConcurrentHashMap<>();

    private LavalinkManager(String token) {
        this.client = new LavalinkClient(Helpers.getUserIdFromToken(token));

        String url = value("LAVALINK_URL", DEFAULT_URL);

        client.addNode(new NodeOptions.Builder()
                .setName("main")
                .setServerUri(URI.create(url))
                .setPassword(value("LAVALINK_PASSWORD", DEFAULT_PASSWORD))
                .build());

        LOG.info("Lavalink: узел {}", url);

        subscribeEvents();
    }

    public static LavalinkManager start(String token) {
        LavalinkManager local = instance;

        if (local == null) {
            synchronized (LavalinkManager.class) {
                local = instance;
                if (local == null) {
                    instance = local = new LavalinkManager(token);
                }
            }
        }
        return local;
    }

    public static LavalinkManager getInstance() {
        LavalinkManager local = instance;

        if (local == null) {
            throw new IllegalStateException("LavalinkManager не запущен");
        }
        return local;
    }

    public static void shutdownIfStarted() {
        LavalinkManager local = instance;

        if (local != null) {
            local.client.close();
        }
    }

    
    // перехватчик голосовых событий для JDA
    public VoiceDispatchInterceptor voiceUpdateListener() {
        JDAVoiceUpdateListener delegate = new JDAVoiceUpdateListener(client);

        return new VoiceDispatchInterceptor() {
            @Override
            public void onVoiceServerUpdate(VoiceServerUpdate update) {
                try {
                    delegate.onVoiceServerUpdate(update);
                } catch (RuntimeException e) {
                    LOG.warn("Голосовое обновление не дошло до узла: {}", e.getMessage());
                }
            }

            @Override
            public boolean onVoiceStateUpdate(VoiceStateUpdate update) {
                try {
                    return delegate.onVoiceStateUpdate(update);
                } catch (RuntimeException e) {
                    LOG.warn("Смена голосового состояния не дошла до узла: {}", e.getMessage());
                    return false;
                }
            }
        };
    }

    // готов ли узел принимать команды
    public boolean isNodeAvailable() {
        return client.getNodes().stream().anyMatch(LavalinkNode::getAvailable);
    }

    public GuildMusic getMusic(long guildId) {
        return guilds.computeIfAbsent(guildId, id -> new GuildMusic());
    }

    public Link getLink(long guildId) {
        return client.getOrCreateLink(guildId);
    }

    // играющий трек или null
    public Track getPlayingTrack(long guildId) {
        Link link = client.getLinkIfCached(guildId);

        if (link == null) {
            return null;
        }

        LavalinkPlayer player = link.getCachedPlayer();

        return player == null ? null : player.getTrack();
    }

    public LavalinkPlayer getPlayer(long guildId) {
        Link link = client.getLinkIfCached(guildId);

        return link == null ? null : link.getCachedPlayer();
    }

    // loadandplay как у lavaplayer
    public void loadAndPlay(GuildMessageChannel channel, String identifier, String member, String fileName) {
        long guildId = channel.getGuild().getIdLong();
        GuildMusic music = getMusic(guildId);

        music.setAnnounceChannel(channel);

        getLink(guildId).loadItem(identifier).subscribe(result -> {
            if (result instanceof TrackLoaded loaded) {
                queue(guildId, loaded.getTrack());
                announceTrack(channel, loaded.getTrack(), member, fileName);
                return;
            }

            if (result instanceof SearchResult search) {
                List<Track> tracks = search.getTracks();

                if (tracks.isEmpty()) {
                    channel.sendMessage("🔴 Ничего не нашёл по этому запросу!").queue();
                    return;
                }

                queue(guildId, tracks.get(0));
                announceTrack(channel, tracks.get(0), member, fileName);
                return;
            }

            if (result instanceof PlaylistLoaded playlist) {
                List<Track> tracks = playlist.getTracks();

                tracks.forEach(track -> queue(guildId, track));

                channel.sendMessage("Добавляю в очередь: `" + tracks.size()
                                + "` треков из `" + playlist.getInfo().getName() + "`\nПоставил: `" + member + "`")
                        .queue();
                return;
            }

            if (result instanceof NoMatches) {
                LOG.info("Ничего не найдено по запросу {}", identifier);
                channel.sendMessage("🔴 Ничего не нашёл по этому запросу!").queue();
                return;
            }

            if (result instanceof LoadFailed failed) {
                LOG.warn("Не удалось загрузить {}: {}", identifier, failed.getException().getMessage());
                channel.sendMessage("🔴 Не удалось загрузить трек!").queue();
            }
        }, error -> {
            LOG.error("Запрос к Lavalink по {} сорвался", identifier, error);
            channel.sendMessage("🔴 Музыкальный сервер не отвечает.").queue();
        });
    }

    // ставит трек в очередь или запускает сразу
    public void queue(long guildId, Track track) {
        GuildMusic music = getMusic(guildId);

        if (getPlayingTrack(guildId) != null) {
            music.queue.offer(track);
            return;
        }

        play(guildId, track);
    }

    public void nextTrack(long guildId) {
        GuildMusic music = getMusic(guildId);

        music.clearRetried();

        Track next = music.queue.poll();

        if (next == null) {
            LOG.info("Очередь пуста, играть нечего");
            getLink(guildId).updatePlayer(update -> update.stopTrack()).subscribe();
            return;
        }

        play(guildId, next);
    }

    private void play(long guildId, Track track) {
        getLink(guildId).updatePlayer(update -> update.setTrack(track)).subscribe(
                player -> {},
                error -> {
                    LOG.error("Не удалось запустить трек {}", track.getInfo().getIdentifier(), error);
                    getMusic(guildId).announce("🔴 Не смог включить трек, попробуй ещё раз.");
                });
    }

    private void subscribeEvents() {
        client.on(TrackStartEvent.class).subscribe(event -> {
            TrackInfo info = event.getTrack().getInfo();

            LOG.info("Трек {} начался: {}", info.getIdentifier(), info.getTitle());
        });

        client.on(TrackEndEvent.class).subscribe(this::onTrackEnd);

        client.on(TrackExceptionEvent.class).subscribe(event ->
                LOG.warn("Ошибка воспроизведения {}: {}",
                        event.getTrack().getInfo().getIdentifier(), event.getException().getMessage()));

        client.on(TrackStuckEvent.class).subscribe(event -> {
            TrackInfo info = event.getTrack().getInfo();

            LOG.warn("Трек {} завис более чем на {} мс", info.getIdentifier(), event.getThresholdMs());

            getMusic(event.getGuildId()).announce("🔴 Трек `" + info.getTitle() + "` завис, пропускаю.");
            nextTrack(event.getGuildId());
        });

        client.on(WebSocketClosedEvent.class).subscribe(event ->
                LOG.warn("Голосовое соединение закрыто: код {}, причина {}, по инициативе Discord {}",
                        event.getCode(), event.getReason(), event.getByRemote()));
    }

    private void onTrackEnd(TrackEndEvent event) {
        long guildId = event.getGuildId();
        Track track = event.getTrack();
        TrackInfo info = track.getInfo();
        AudioTrackEndReason reason = event.getEndReason();
        GuildMusic music = getMusic(guildId);

        LOG.info("Трек {} закончился: {}, в очереди {}", info.getIdentifier(), reason, music.queue.size());

        if (!reason.getMayStartNext()) {
            return;
        }

        if (reason == AudioTrackEndReason.LOAD_FAILED) {
            if (music.markRetried(info.getIdentifier())) {
                LOG.info("Повторная попытка для {}", info.getIdentifier());
                play(guildId, track.makeClone());
                return;
            }

            music.announce("🔴 Не смог проиграть `" + info.getTitle() + "`, пропускаю.");
            nextTrack(guildId);
            return;
        }

        // повтор упавшего трека зациклил бы ту же ошибку навсегда
        if (music.looping) {
            play(guildId, track.makeClone());
            return;
        }

        nextTrack(guildId);
    }

    private static void announceTrack(GuildMessageChannel channel, Track track, String member, String fileName) {
        TrackInfo info = track.getInfo();
        String title = fileName != null ? fileName : info.getTitle();

        channel.sendMessage("Добавляю в очередь: `" + title
                        + "`\nАвтор: `" + info.getAuthor()
                        + "`\nПоставил: `" + member + "`")
                .queue();
    }

    private static String value(String key, String fallback) {
        String value = Config.get(key);

        return value == null || value.isBlank() ? fallback : value;
    }
}
