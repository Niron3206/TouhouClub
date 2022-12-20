package ru.niron3206.audioplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, MusicManager> musicManager;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManager = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public MusicManager getMusicManager(Guild guild) {
        return musicManager.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final MusicManager musicManager = new MusicManager(audioPlayerManager);

            guild.getAudioManager().setSendingHandler(musicManager.getHandler());

            return musicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, String trackUrl) {
        final MusicManager musicManager = getMusicManager(channel.getGuild());
        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);

                channel.sendMessage("Добавляю в очередь: `")
                        .append(audioTrack.getInfo().title)
                        .append("`\nАвтор: `")
                        .append(audioTrack.getInfo().author)
                        .append("`")
                        .queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack track = audioPlaylist.getTracks().get(0);
                musicManager.scheduler.queue(track);

                channel.sendMessage("Добавляю в очередь: `")
                        .append(track.getInfo().title)
                        .append("`\nАвтор: `")
                        .append(track.getInfo().author)
                        .append("`")
                        .queue();
                /*
                List<AudioTrack> tracks = audioPlaylist.getTracks();
                channel.sendMessage("Добавляю в очередь: `")
                        .append(String.valueOf(tracks.size()))
                        .append(" треков из ")
                        .append(audioPlaylist.getName())
                        .append("`")
                        .queue();

                for (AudioTrack track : tracks) {
                    musicManager.scheduler.queue(track);
                }
                */
            }

            @Override
            public void noMatches() {
                //
            }

            @Override
            public void loadFailed(FriendlyException e) {
                //
            }
        });
    }

    public static PlayerManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
