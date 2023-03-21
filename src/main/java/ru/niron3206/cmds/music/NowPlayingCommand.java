package ru.niron3206.cmds.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.ICommand;
import ru.niron3206.audioplayer.MusicManager;
import ru.niron3206.audioplayer.PlayerManager;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class NowPlayingCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getChannel().asTextChannel();
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
        AudioTrack track = audioPlayer.getPlayingTrack();

        if(track == null) {
            channel.sendMessage("Никакой музыки не проигрывается...").queue();
            return;
        }

        AudioTrackInfo info = track.getInfo();
        channel.sendMessageFormat("Сейчас играет: `%s`\nАвтор: `%s`\n(Ссылка: <%s>)", info.title, info.author, info.uri).queue();
    }

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getHelp() {
        return "Показывает проигрывающийся трек";
    }

    @Override
    public List<String> getAliases() {
        return List.of("np", "NP");
    }
}
