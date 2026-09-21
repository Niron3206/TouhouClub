package ru.niron3206.audioplayer;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class AutoLeave {

    private static final Logger LOG = LoggerFactory.getLogger(AutoLeave.class);

    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor(task -> {
                Thread thread = new Thread(task, "AutoLeave");
                thread.setDaemon(true);
                return thread;
            });

    private static final Map<Long, ScheduledFuture<?>> WATCHERS = new ConcurrentHashMap<>();
    private static final Map<Long, Integer> IDLE_CHECKS = new ConcurrentHashMap<>();

    private AutoLeave() {}

    public static void watch(Guild guild) {
        long guildId = guild.getIdLong();

        WATCHERS.computeIfAbsent(guildId, id -> SCHEDULER.scheduleWithFixedDelay(
                () -> check(guild), 60, 60, TimeUnit.SECONDS));
    }

    public static void cancel(Guild guild) {
        IDLE_CHECKS.remove(guild.getIdLong());
        ScheduledFuture<?> watcher = WATCHERS.remove(guild.getIdLong());

        if (watcher != null) {
            watcher.cancel(false);
        }
    }

    private static void check(Guild guild) {
        // исключение отсюда навсегда отменило бы периодическую задачу
        try {
            GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();
            AudioChannelUnion channel = selfVoiceState == null ? null : selfVoiceState.getChannel();

            if (channel == null) {
                cancel(guild);
                return;
            }

            long guildId = guild.getIdLong();
            LavalinkManager lavalink = LavalinkManager.getInstance();

            boolean nothingToPlay = lavalink.getPlayingTrack(guildId) == null
                    && lavalink.getMusic(guildId).queue.isEmpty();
            // сам бот тоже числится участником канала, поэтому считаем только людей
            boolean noListeners = channel.getMembers().stream()
                    .noneMatch(member -> !member.getUser().isBot());

            if (noListeners) {
                LOG.debug("Выхожу из канала {}: слушателей не осталось", channel.getId());
                leave(guild);
                return;
            }

            if (!nothingToPlay) {
                IDLE_CHECKS.remove(guildId);
                return;
            }

            int idleChecks = IDLE_CHECKS.merge(guildId, 1, Integer::sum);

            if (idleChecks < 2) {
                LOG.debug("Канал {} простаивает, жду ещё одну проверку", channel.getId());
                return;
            }

            LOG.debug("Выхожу из канала {}: играть нечего", channel.getId());
            leave(guild);
        } catch (Exception e) {
            LOG.error("Проверка голосового канала сервера {} сорвалась", guild.getId(), e);
        }
    }

    // чистит очередь,гасит плеер на узле и уводит бота из канала
    public static void leave(Guild guild) {
        long guildId = guild.getIdLong();
        LavalinkManager lavalink = LavalinkManager.getInstance();
        GuildMusic music = lavalink.getMusic(guildId);

        music.looping = false;
        music.queue.clear();

        lavalink.getLink(guildId).destroy().subscribe();
        guild.getJDA().getDirectAudioController().disconnect(guild);

        cancel(guild);
    }
}
