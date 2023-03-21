package ru.niron3206;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Main {

    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createDefault(Config.get("TOKEN"))
                .setActivity(Activity.listening("my master."))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .build();
        jda.addEventListener(new Listener());
    }
}
