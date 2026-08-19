package ru.niron3206;

import club.minnced.discord.jdave.interop.JDaveSessionFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {

    public static JDA jda;

    public static void main(String[] args) {
        jda = JDABuilder.createDefault(Config.require("TOKEN"))
                .setActivity(Activity.listening("my master."))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.VOICE_STATE)
                .setAudioModuleConfig(
                        // без DAVE дискорд не пускает в голосовые каналы
                        new AudioModuleConfig()
                                .withDaveSessionFactory(new JDaveSessionFactory())
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .addEventListeners(new Listener())
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(jda::shutdown, "Shutdown"));
    }
}
