package ru.niron3206.cmds.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.niron3206.audioplayer.MusicManager;
import ru.niron3206.audioplayer.PlayerManager;
import ru.niron3206.cmds.CommandContext;
import ru.niron3206.cmds.Groups;
import ru.niron3206.cmds.ICommand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@SuppressWarnings("ConstantConditions")
public class RemoveCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getGuildChannel().asTextChannel();
        Member self = ctx.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            channel.sendMessage("\uD83D\uDD34 Я должен находиться в голосовом канале!").queue();
            return;
        }

        Member member = ctx.getEvent().getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inAudioChannel()) {
            channel.sendMessage("\uD83D\uDD34 Ты должен зайти в голосовой канал!").queue();
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            channel.sendMessage("\uD83D\uDD34 Мы должны быть в одном и том же канале!").queue();
            return;
        }

        MusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        List<AudioTrack> trackList = new ArrayList<>(queue);

        AudioTrack trackToDelete = trackList.get(Integer.parseInt(ctx.getArgs().get(0)) - 1);

        musicManager.scheduler.queue.remove(trackToDelete);

        channel.sendMessage("\uD83C\uDFB5 Трек `" + trackToDelete.getInfo().title + "` был удалён из очереди!").queue();

    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getHelp() {
        return "Удаляет выбранный трек из очереди\nКак использовать: `~remove <номерь трека в очереди>`";
    }

    @Override
    public Groups getGroup() {
        return Groups.MUSIC;
    }

    @Override
    public List<String> getAliases() {
        return List.of("r", "R");
    }
}
