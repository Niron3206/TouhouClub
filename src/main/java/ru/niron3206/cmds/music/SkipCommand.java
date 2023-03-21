package ru.niron3206.cmds.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.ICommand;
import ru.niron3206.audioplayer.MusicManager;
import ru.niron3206.audioplayer.PlayerManager;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SkipCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getGuildChannel().asTextChannel();
        Member self = ctx.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            channel.sendMessage("Я должен находиться в голосовом канале!").queue();
            return;
        }

        Member member = ctx.getEvent().getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()) {
            channel.sendMessage("Ты должен зайти в голосовой канал, чтобы включить свою классную музычку!").queue();
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            channel.sendMessage("Мы должны быть в одном и том же канале!").queue();
            return;
        }

        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        AudioPlayer audioPlayer = musicManager.audioPlayer;

        if(audioPlayer.getPlayingTrack() == null) {
            channel.sendMessage("Никакой музыки не проигрывается...").queue();
            return;
        }

        musicManager.scheduler.nextTrack();

        channel.sendMessage("Проигрывание текущего трека было прекращено!").queue();
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getHelp() {
        return "Пропускает играющий трек.";
    }

    @Override
    public List<String> getAliases() {
        return List.of("s", "S");
    }
}
