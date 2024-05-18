package cotato.csquiz.service;

import cotato.csquiz.controller.dto.generation.AddGenerationRequest;
import cotato.csquiz.controller.dto.generation.AddGenerationResponse;
import cotato.csquiz.controller.dto.generation.ChangeGenerationPeriodRequest;
import cotato.csquiz.controller.dto.generation.ChangeRecruitingStatusRequest;
import cotato.csquiz.controller.dto.generation.GenerationInfoResponse;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GenerationService {

    private final GenerationRepository generationRepository;

    @Transactional
    public AddGenerationResponse addGeneration(AddGenerationRequest request) {
        checkPeriodValid(request.startDate(), request.endDate());
        checkNumberValid(request.generationNumber());
        Generation generation = Generation.builder()
                .number(request.generationNumber())
                .startDate(request.startDate())
                .endDate(request.endDate())
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
        generation.changePeriod(request.startDate(), request.endDate());
        log.info("[기수 기간 변경 성공]: 시작: {} ~ 끝: {}", request.startDate(), request.endDate());
    }

    public List<GenerationInfoResponse> findGenerations() {
        List<Generation> generations = generationRepository.findAll();
        return generations.stream()
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
}
