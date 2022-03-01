package ru.niron3206;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main {

    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createDefault(Config.get("TOKEN"))
                .setActivity(Activity.listening("my master."))
                .build();
        jda.addEventListener(new Listener());
    }
}
