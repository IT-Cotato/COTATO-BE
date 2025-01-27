package org.cotato.csquiz.domain.generation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import org.cotato.csquiz.api.generation.dto.GenerationInfoResponse;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.embedded.GenerationPeriod;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GenerationServiceTest {

    @Mock
    private GenerationRepository generationRepository;

    @InjectMocks
    private GenerationService generationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito mock 객체 초기화
    }

    @Test
    void 현재_날짜에_기수가_존재할_경우_현재_기수_반환() {
        // given: 기수 데이터 설정
        Generation currentGeneration = Generation.builder()
                .number(2)
                .period(GenerationPeriod.of(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 10, 1)))
                .build();

        // when: 현재 날짜에 해당하는 기수 반환
        when(generationRepository.findByCurrentDate(any(LocalDate.class))).thenReturn(Optional.of(currentGeneration));

        LocalDate currentDate = LocalDate.of(2024, 7, 15);
        GenerationInfoResponse generationInfo = generationService.findCurrentGeneration(currentDate);

        // then: 2기 반환
        assertThat(generationInfo.generationNumber()).isEqualTo(2);
    }

    @Test
    void 현재_날짜에_기수가_존재하지_않을_경우_이전_기수_반환() {
        // given
        Generation previousGeneration = Generation.builder()
                .number(2)
                .period(GenerationPeriod.of(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 10, 1)))
                .build();

        // when: 현재 날짜에 해당하는 기수가 없을 때 이전 기수를 반환
        when(generationRepository.findByCurrentDate(any(LocalDate.class))).thenReturn(Optional.empty());
        when(generationRepository.findPreviousGenerationByCurrentDate(any(LocalDate.class))).thenReturn(Optional.of(previousGeneration));

        LocalDate currentDate = LocalDate.of(2024, 10, 2);
        GenerationInfoResponse generationInfo = generationService.findCurrentGeneration(currentDate);

        // then: 2기가 반환되어야 함
        assertThat(generationInfo.generationNumber()).isEqualTo(2);
    }

    @Test
    void 이전_기수가_없을_경우_예외_발생() {
        // when: 이전 기수가 존재하지 않을 경우
        when(generationRepository.findByCurrentDate(any(LocalDate.class))).thenReturn(Optional.empty());
        when(generationRepository.findPreviousGenerationByCurrentDate(any(LocalDate.class))).thenReturn(Optional.empty());

        LocalDate currentDate = LocalDate.of(2023, 12, 31);

        // then: 에러 발생
        assertThrows(EntityNotFoundException.class, () -> {
            generationService.findCurrentGeneration(currentDate);
        });
    }
}