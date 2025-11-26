package org.cotato.csquiz.domain.education.service.component;

import java.util.Optional;

import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.education.entity.Choice;
import org.cotato.csquiz.domain.education.enums.ChoiceCorrect;
import org.cotato.csquiz.domain.education.repository.ChoiceRepository;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordButtonListener extends ListenerAdapter {

	private final ChoiceRepository choiceRepository;

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		String id = Optional.ofNullable(event.getButton().getId())
			.orElseThrow(() -> new AppException(ErrorCode.DISCORD_BUTTON_ERROR));
		log.info("[디스 코드 리스너 전송: {}]", id);

		Choice choice = choiceRepository.findById(Long.parseLong(id))
			.orElseThrow(() -> new EntityNotFoundException("해당 선지를 찾을 수 없습니다."));

		if (choice.getIsCorrect() == ChoiceCorrect.ANSWER) {
			event.deferReply(true).setContent("축하합니다! 정답입니다.").queue();
		} else {
			event.deferReply(true).setContent("아쉽게도 오답이네요 ㅠ").queue();
		}
	}
}
