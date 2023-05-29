package ru.niron3206.audioplayer;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;

import java.util.Timer;
import java.util.TimerTask;

public class AutoLeave {

    private final Guild guild;

    public AutoLeave(Guild guild) {
        this.guild = guild;
    }

    public void timer() {
        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();
                System.out.println("I'm in voice chat (ID: " + selfVoiceState.getChannel().getId() + ")");

                if (musicManager.audioPlayer.getPlayingTrack() == null
                        && musicManager.scheduler.queue.isEmpty()) {
                    musicManager.scheduler.looping = false;
                    musicManager.audioPlayer.stopTrack();
                    guild.getAudioManager().closeAudioConnection();
                    cancel();
                }
            }
        };

        Timer timer = new Timer("BotInVoice");

        timer.scheduleAtFixedRate(timerTask, 5000, 60000);
    }
}
