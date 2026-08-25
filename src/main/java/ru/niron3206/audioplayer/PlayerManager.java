package ru.niron3206.audioplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.AndroidVrWithThumbnail;
import dev.lavalink.youtube.clients.MWebWithThumbnail;
import dev.lavalink.youtube.clients.MusicWithThumbnail;
import dev.lavalink.youtube.clients.Tv;
import dev.lavalink.youtube.clients.TvHtml5SimplyWithThumbnail;
import dev.lavalink.youtube.clients.Web;
import dev.lavalink.youtube.clients.WebEmbeddedWithThumbnail;
import dev.lavalink.youtube.clients.WebWithThumbnail;
import dev.lavalink.youtube.clients.skeleton.Client;
import dev.lavalink.youtube.cipher.RemoteCipherManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.niron3206.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerManager.class);

    private static volatile PlayerManager instance;

    private final Map<Long, MusicManager> musicManagers = new ConcurrentHashMap<>();
    private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

    private PlayerManager() {
        audioPlayerManager.registerSourceManager(createYoutubeSource());
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);

        // без порога зависший трек держал бы очередь бесконечно
        audioPlayerManager.setTrackStuckThreshold(15_000);
        audioPlayerManager.setFrameBufferDuration(5_000);
    }

    private YoutubeAudioSourceManager createYoutubeSource() {
        String refreshToken = Config.get("YT_OAUTH_REFRESH_TOKEN");
        boolean withOauth = isSet(refreshToken) || "true".equalsIgnoreCase(Config.get("YT_OAUTH"));

        List<Client> clients = new ArrayList<>();

        if (withOauth) {
            clients.add(new Tv());
        }

        // клиенты опрашиваются по порядку
        Collections.addAll(clients,
                new MusicWithThumbnail(),
                new TvHtml5SimplyWithThumbnail(),
                new AndroidVrWithThumbnail(),
                new MWebWithThumbnail(),
                new WebWithThumbnail(),
                new WebEmbeddedWithThumbnail());

        YoutubeAudioSourceManager youtube =
                new YoutubeAudioSourceManager(true, clients.toArray(new Client[0]));

        // lavaplayer даёт по 3 секунды на соединение и на чтение. Этого мало и превышение прилетает как ошибка и роняет трек целиком
        youtube.getHttpInterfaceManager().configureRequests(config ->
                RequestConfig.copy(config)
                        .setConnectTimeout(10_000)
                        .setConnectionRequestTimeout(10_000)
                        .setSocketTimeout(15_000)
                        .build());

        // в 1.18.2 встроенный разбор player script ломается на текущем ютубчике
        // ("must find sig function")
        String cipherUrl = Config.get("YT_CIPHER_URL");

        if (isSet(cipherUrl)) {
            youtube.setCipherManager(new RemoteCipherManager(cipherUrl));
            LOG.info("YouTube: расшифровка подписи вынесена на {}", cipherUrl);
        }

        String poToken = Config.get("YT_PO_TOKEN");
        String visitorData = Config.get("YT_VISITOR_DATA");

        if (isSet(poToken) && isSet(visitorData)) {
            Web.setPoTokenAndVisitorData(poToken, visitorData);
            LOG.info("YouTube: включён poToken");
        }

        if (isSet(refreshToken)) {
            youtube.useOauth2(refreshToken, true);
            LOG.info("YouTube: включён OAuth по сохранённому refresh token");
        } else if (withOauth) {
            // ссылка, код для входа и refresh token появятся в логе
            youtube.useOauth2(null, false);
            LOG.info("YouTube: запущен вход через OAuth");
        }

        return youtube;
    }

    private static boolean isSet(String value) {
        return value != null && !value.isBlank();
    }

    public MusicManager getMusicManager(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), id -> {
            MusicManager musicManager = new MusicManager(audioPlayerManager);
            guild.getAudioManager().setSendingHandler(musicManager.getHandler());
            return musicManager;
        });
    }

    public void loadAndPlay(GuildMessageChannel channel, String trackUrl, String member, String fileName) {
        MusicManager musicManager = getMusicManager(channel.getGuild());
        musicManager.scheduler.setAnnounceChannel(channel);

        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);
                announceTrack(channel, track, member, fileName);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    AudioTrack track = playlist.getSelectedTrack() != null
                            ? playlist.getSelectedTrack()
                            : playlist.getTracks().get(0);

                    musicManager.scheduler.queue(track);
                    announceTrack(channel, track, member, fileName);
                    return;
                }

                List<AudioTrack> tracks = playlist.getTracks();
                tracks.forEach(musicManager.scheduler::queue);

                channel.sendMessage("Добавляю в очередь: `" + tracks.size()
                                + "` треков из `" + playlist.getName() + "`\nПоставил: `" + member + "`")
                        .queue();
            }

            @Override
            public void noMatches() {
                LOG.info("Ничего не найдено по запросу {}", trackUrl);
                channel.sendMessage("🔴 Ничего не нашёл по этому запросу!").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                LOG.warn("Не удалось загрузить {}", trackUrl, e);
                channel.sendMessage("🔴 Не удалось загрузить трек!").queue();
            }
        });
    }

    private static void announceTrack(GuildMessageChannel channel, AudioTrack track, String member, String fileName) {
        String title = fileName != null ? fileName : track.getInfo().title;

        channel.sendMessage("Добавляю в очередь: `" + title
                        + "`\nАвтор: `" + track.getInfo().author
                        + "`\nПоставил: `" + member + "`")
                .queue();
    }

    public static PlayerManager getInstance() {
        PlayerManager local = instance;

        if (local == null) {
            synchronized (PlayerManager.class) {
                local = instance;
                if (local == null) {
                    instance = local = new PlayerManager();
                }
            }
        }
        return local;
    }

    public static void shutdownIfStarted() {
        PlayerManager local = instance;

        if (local != null) {
            local.audioPlayerManager.shutdown();
        }
    }
}
