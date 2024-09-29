package org.cotato.csquiz.common.config;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.cotato.csquiz.domain.education.service.DiscordButtonListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JdaConfig {

    private final DiscordButtonListener discordButtonListener;

    @Value("${discord.bot.token}")
    private String botToken;

    @Bean
    public JDA jda() throws InterruptedException {
        return JDABuilder.createDefault(botToken)
                .addEventListeners(discordButtonListener)
                .build().awaitReady();
    }
}
