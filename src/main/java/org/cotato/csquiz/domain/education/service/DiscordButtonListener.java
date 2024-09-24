package org.cotato.csquiz.domain.education.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.education.entity.Choice;
import org.cotato.csquiz.domain.education.enums.ChoiceCorrect;
import org.cotato.csquiz.domain.education.repository.ChoiceRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordButtonListener extends ListenerAdapter {

    private final ChoiceRepository choiceRepository;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = Optional.ofNullable(event.getButton().getId())
                .orElseThrow(() -> new AppException(ErrorCode.DISCORD_BUTTON_ERROR));

        Choice choice = choiceRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new EntityNotFoundException("해당 선지를 찾을 수 없습니다."));

        if (choice.getIsCorrect() == ChoiceCorrect.ANSWER) {
            event.reply("축하합니다! 정답입니다.").queue();
        } else {
            event.reply("아쉽게도 오답이네요 ㅠ").queue();
        }
    }
}
