package ru.niron3206.cmds.music;

import ru.niron3206.audioplayer.MusicManager;
import ru.niron3206.cmds.CommandContext;

import java.util.List;

public class LoopCommand extends MusicCommand {

    @Override
    protected void handleMusic(CommandContext ctx, MusicManager musicManager) {
        boolean looping = !musicManager.scheduler.looping;

        musicManager.scheduler.looping = looping;

        ctx.getChannel().sendMessageFormat("**%s**", looping
                        ? "🔁 Повторное проигрывание включено"
                        : "Повторное проигрывание выключено")
                .queue();
    }

    @Override
    public String getName() {
        return "loop";
    }

    @Override
    public List<String> getAliases() {
        return List.of("l");
    }

    @Override
    public String getHelp() {
        return "Ставит на повтор текущий/последующие трек/и";
    }
}
