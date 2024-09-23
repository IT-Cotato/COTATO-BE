package org.cotato.csquiz.common.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdaConfig {

    @Value("${discord.bot.token}")
    private String botToken;

    @Bean
    public JDA jda() throws InterruptedException {
        return JDABuilder.createDefault(botToken).build().awaitReady();
    }
}
