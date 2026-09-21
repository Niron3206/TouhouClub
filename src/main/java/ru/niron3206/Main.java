package ru.niron3206;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.niron3206.audioplayer.LavalinkManager;

public class Main {

    public static JDA jda;

    public static void main(String[] args) {
        String token = Config.require("TOKEN");

        LavalinkManager lavalink = LavalinkManager.start(token);

        try {
            jda = buildJda(token, lavalink);
        } catch (RuntimeException e) {
            // иначе клиент Lavalink держит процесс живым и контейнер висит вместо перезапуска
            LavalinkManager.shutdownIfStarted();
            throw e;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jda.shutdown();
            LavalinkManager.shutdownIfStarted();
        }, "Shutdown"));
    }

    private static JDA buildJda(String token, LavalinkManager lavalink) {
        return JDABuilder.createDefault(token)
                .setActivity(Activity.listening("my master."))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.VOICE_STATE)
                .setVoiceDispatchInterceptor(lavalink.voiceUpdateListener())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .addEventListeners(new Listener())
                .build();
    }
}
