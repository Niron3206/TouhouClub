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
import ru.niron3206.util.TimeFormat;
import ru.niron3206.util.YoutubeLinks;

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

    // ключ userData с позицией старта
    private static final String START_KEY = "start";

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
                Track track = loaded.getTrack();
                long start = applyStart(track, identifier);

                queue(guildId, track);
                announceTrack(channel, track, member, fileName, start);
                return;
            }

            if (result instanceof SearchResult search) {
                List<Track> tracks = search.getTracks();

                if (tracks.isEmpty()) {
                    channel.sendMessage("🔴 Ничего не нашёл по этому запросу!").queue();
                    return;
                }

                queue(guildId, tracks.get(0));
                announceTrack(channel, tracks.get(0), member, fileName, 0);
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

        Track next = music.next();

        if (next == null) {
            LOG.info("Очередь пуста, играть нечего");
            getLink(guildId).updatePlayer(update -> update.stopTrack()).subscribe();
            return;
        }

        play(guildId, next);
    }

    private void play(long guildId, Track track) {
        long start = startOf(track);

        getLink(guildId).updatePlayer(update -> {
            update.setTrack(track);

            // setTrack берёт позицию из info, а там 0
            if (start > 0) {
                update.setPosition(start);
            }
        }).subscribe(
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
            Track track = event.getTrack();

            LOG.warn("Трек {} завис более чем на {} мс", track.getInfo().getIdentifier(), event.getThresholdMs());

            // ютуб перебирает клиентов дольше порога, со второй попытки обычно отдаёт поток
            retryOrSkip(event.getGuildId(), track, "🔴 Трек `" + track.getInfo().getTitle() + "` завис, пропускаю.");
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
            retryOrSkip(guildId, track, "🔴 Не смог проиграть `" + info.getTitle() + "`, пропускаю.");
            return;
        }

        // повтор упавшего трека зациклил бы ту же ошибку навсегда
        if (music.looping) {
            play(guildId, track.makeClone());
            return;
        }

        nextTrack(guildId);
    }

    // одна попытка на трек на все виды сбоев. вторая неудача, тогда пропуск
    private void retryOrSkip(long guildId, Track track, String skipMessage) {
        String identifier = track.getInfo().getIdentifier();

        if (getMusic(guildId).markRetried(identifier)) {
            LOG.info("Повторная попытка для {}", identifier);
            play(guildId, track.makeClone());
            return;
        }

        getMusic(guildId).announce(skipMessage);
        nextTrack(guildId);
    }

    // отметка ?t= из ссылки ютуба едет с треком в userData до запуска,
    // makeClone её копирует, поэтому повтор и loop тоже начинают с неё
    private static long applyStart(Track track, String identifier) {
        long start = YoutubeLinks.startTime(identifier);

        if (start <= 0) {
            return 0;
        }

        TrackInfo info = track.getInfo();

        if (info.isStream() || !info.isSeekable() || start >= info.getLength()) {
            LOG.info("Отметка {} мс не подходит для {}, играю с начала", start, info.getIdentifier());
            return 0;
        }

        track.setUserData(Map.of(START_KEY, start));
        return start;
    }

    private static long startOf(Track track) {
        Object start = track.getUserData(Map.class).get(START_KEY);

        return start instanceof Number number ? number.longValue() : 0;
    }

    private static void announceTrack(GuildMessageChannel channel, Track track, String member, String fileName, long start) {
        TrackInfo info = track.getInfo();
        String title = fileName != null ? fileName : info.getTitle();
        String startLine = start > 0 ? "\nС отметки: `" + TimeFormat.format(start) + "`" : "";

        channel.sendMessage("Добавляю в очередь: `" + title
                        + "`\nАвтор: `" + info.getAuthor() + "`"
                        + startLine
                        + "\nПоставил: `" + member + "`")
                .queue();
    }

    private static String value(String key, String fallback) {
        String value = Config.get(key);

        return value == null || value.isBlank() ? fallback : value;
    }
}
