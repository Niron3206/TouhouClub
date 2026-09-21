package ru.niron3206.cmds.music;

import ru.niron3206.audioplayer.GuildMusic;
import ru.niron3206.audioplayer.LavalinkManager;
import ru.niron3206.cmds.CommandContext;

public class StopCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, GuildMusic music) {
        music.queue.clear();

        LavalinkManager.getInstance()
                .getLink(ctx.getGuild().getIdLong())
                .updatePlayer(update -> update.stopTrack())
                .subscribe();

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
