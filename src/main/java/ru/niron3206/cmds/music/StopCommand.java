package ru.niron3206.cmds.music;

import ru.niron3206.audioplayer.MusicManager;
import ru.niron3206.cmds.CommandContext;

public class StopCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, MusicManager musicManager) {
        musicManager.scheduler.queue.clear();
        musicManager.audioPlayer.stopTrack();

        ctx.getChannel().sendMessage("🧹 Проигрывание было прекращено и очередь треков была очищена!").queue();
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getHelp() {
        return "Останавливает все песни и очищает очередь";
    }
}
