package org.cotato.csquiz.domain.generation.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.generation.dto.AddGenerationRequest;
import org.cotato.csquiz.api.generation.dto.AddGenerationResponse;
import org.cotato.csquiz.api.generation.dto.ChangeGenerationPeriodRequest;
import org.cotato.csquiz.api.generation.dto.ChangeRecruitingStatusRequest;
import org.cotato.csquiz.api.generation.dto.GenerationInfoResponse;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.generation.embedded.GenerationPeriod;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GenerationService {

    private static final Integer BASE_NUMBER = 1;
    private final GenerationRepository generationRepository;

    @Transactional
    public AddGenerationResponse addGeneration(AddGenerationRequest request) {
        checkPeriodValid(request.startDate(), request.endDate());
        checkNumberValid(request.generationNumber());
        Generation generation = Generation.builder()
                .number(request.generationNumber())
                .period(GenerationPeriod.of(request.startDate(), request.endDate()))
                .sessionCount(request.sessionCount())
                .build();
        Generation savedGeneration = generationRepository.save(generation);
        return AddGenerationResponse.from(savedGeneration);
    }

    @Transactional
    public void changeRecruitingStatus(ChangeRecruitingStatusRequest request) {
        Generation generation = generationRepository.findById(request.generationId())
                .orElseThrow(() -> new EntityNotFoundException("찾으려는 기수가 존재하지 않습니다."));
        generation.changeRecruit(request.statement());
        log.info("[기수 모집 상태 변경 성공]: {}", request.statement());
    }

    @Transactional
    public void changeGenerationPeriod(ChangeGenerationPeriodRequest request) {
        checkPeriodValid(request.startDate(), request.endDate());
        Generation generation = generationRepository.findById(request.generationId())
                .orElseThrow(() -> new EntityNotFoundException("찾으려는 기수가 존재하지 않습니다."));
        generation.changePeriod(GenerationPeriod.of(request.startDate(), request.endDate()));
        log.info("[기수 기간 변경 성공]: 시작: {} ~ 끝: {}", request.startDate(), request.endDate());
    }

    public List<GenerationInfoResponse> findGenerations() {
        return generationRepository.findByNumberGreaterThanEqual(BASE_NUMBER).stream()
                .sorted(Comparator.comparing(Generation::getNumber))
                .map(GenerationInfoResponse::from)
                .toList();
    }

    private void checkPeriodValid(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new AppException(ErrorCode.INVALID_DATE);
        }
    }

    private void checkNumberValid(int generationNumber) {
        if (generationRepository.findByNumber(generationNumber).isPresent()) {
            throw new AppException(ErrorCode.GENERATION_NUMBER_DUPLICATED);
        }
    }

    public GenerationInfoResponse findCurrentGeneration(LocalDate currentDate) {
        Generation currentGeneration = generationRepository.findByCurrentGeneration(currentDate)
                .orElseThrow(() -> new AppException(ErrorCode.ENTITY_NOT_FOUND));
        return GenerationInfoResponse.from(currentGeneration);
    }
}
