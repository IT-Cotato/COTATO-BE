package org.cotato.csquiz.domain.education.service.component;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordConnector {

    private final JDA jda;

    @Value("${discord.guild.id}")
    private String guildId;

    @Value("${discord.channel.id}")
    private String channelId;

    public void sendMessageToTextChannel(MessageEmbed messageEmbed, List<Button> buttons) {
        Guild guild = Optional.ofNullable(jda.getGuildById(guildId))
                .orElseThrow(() -> new AppException(ErrorCode.GUILD_NOT_FOUND));
        TextChannel channel = Optional.ofNullable(guild.getChannelById(TextChannel.class, channelId))
                .orElseThrow(() -> new AppException(ErrorCode.CHANNEL_NOT_FOUND));

        if (channel.canTalk()) {
            channel.sendMessageEmbeds(messageEmbed)
                    .setActionRow(buttons)
                    .queue();
        } else {
            log.warn("채널에 메시지를 보낼 수 없음.");
        }
    }
}
