package org.cotato.csquiz.domain.generation.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.cotato.csquiz.api.generation.dto.GenerationInfoResponse;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.generation.embedded.Period;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;

class GenerationServiceTest {

	@Mock
	private GenerationRepository generationRepository;

	@Mock
	private GenerationReader generationReader;

	@InjectMocks
	private GenerationService generationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);  // Mockito mock 객체 초기화
	}

	@Test
	void whenGenerationExistsForCurrentDate_thenReturnCurrentGeneration() {
		// given: 기수 데이터 설정
		Generation currentGeneration = Generation.builder()
			.number(2)
			.period(Period.of(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 10, 1)))
			.build();

		// when: 현재 날짜에 해당하는 기수 반환
		when(generationRepository.findByCurrentDate(any(LocalDate.class))).thenReturn(Optional.of(currentGeneration));

		LocalDate currentDate = LocalDate.of(2024, 7, 15);
		GenerationInfoResponse generationInfo = generationService.findCurrentGeneration(currentDate);

		// then: 2기 반환
		assertThat(generationInfo.generationNumber()).isEqualTo(2);
	}

	@Test
	void whenGenerationDoesNotExistForCurrentDate_thenReturnPreviousGeneration() {
		// given
		Generation previousGeneration = Generation.builder()
			.number(2)
			.period(Period.of(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 10, 1)))
			.build();

		// when: 현재 날짜에 해당하는 기수가 없을 때 이전 기수를 반환
		when(generationRepository.findByCurrentDate(any(LocalDate.class))).thenReturn(Optional.empty());
		when(generationRepository.findPreviousGenerationByCurrentDate(any(LocalDate.class))).thenReturn(
			Optional.of(previousGeneration));

		LocalDate currentDate = LocalDate.of(2024, 10, 2);
		GenerationInfoResponse generationInfo = generationService.findCurrentGeneration(currentDate);

		// then: 2기가 반환되어야 함
		assertThat(generationInfo.generationNumber()).isEqualTo(2);
	}

	@Test
	void whenPreviousGenerationDoesNotExist_thenThrowException() {
		// when: 이전 기수가 존재하지 않을 경우
		when(generationRepository.findByCurrentDate(any(LocalDate.class))).thenReturn(Optional.empty());
		when(generationRepository.findPreviousGenerationByCurrentDate(any(LocalDate.class))).thenReturn(
			Optional.empty());

		LocalDate currentDate = LocalDate.of(2023, 12, 31);

		// then: 에러 발생
		assertThrows(EntityNotFoundException.class, () -> {
			generationService.findCurrentGeneration(currentDate);
		});
	}

	@Test
	void whenAddGenerationWithStartDateBeforeEndDate_thenThrowException() {
		// given
		LocalDate startDate = LocalDate.of(2025, 6, 1);
		LocalDate endDate = LocalDate.of(2025, 5, 1);
		Integer generationNumber = 3;

		// then: 예외 발생 확인
		assertThrows(AppException.class, () -> {
			generationService.addGeneration(generationNumber, startDate, endDate);
		});
	}

	@Test
	void whenAddGenerationWithOverlappingPeriod_thenThrowException() {
		// given
		LocalDate startDate = LocalDate.of(2025, 6, 1);
		LocalDate endDate = LocalDate.of(2025, 9, 1);
		Integer generationNumber = 3;

		when(generationRepository.existsByPeriod_EndDateGreaterThanEqualAndPeriod_StartDateLessThanEqual(startDate,
			endDate))
			.thenReturn(true);

		// then: 예외 발생 확인
		assertThrows(AppException.class, () -> {
			generationService.addGeneration(generationNumber, startDate, endDate);
		});
	}

	@Test
	void whenAddNewGeneration_thenSuccess() {
		// given
		LocalDate startDate = LocalDate.of(2025, 6, 1);
		LocalDate endDate = LocalDate.of(2025, 9, 1);
		Integer generationNumber = 3;
		Generation newGeneration = Generation.builder()
			.number(generationNumber)
			.period(Period.of(startDate, endDate))
			.build();

		when(generationRepository.existsByPeriod_EndDateGreaterThanEqualAndPeriod_StartDateLessThanEqual(startDate,
			endDate))
			.thenReturn(false);
		when(generationRepository.save(any(Generation.class))).thenReturn(newGeneration);

		ArgumentCaptor<Generation> generationCaptor = ArgumentCaptor.forClass(Generation.class);

		// when
		generationService.addGeneration(generationNumber, startDate, endDate);

		// then
		verify(generationRepository).save(generationCaptor.capture());

		Generation savedGeneration = generationCaptor.getValue();
		assertThat(savedGeneration.getNumber()).isEqualTo(generationNumber);
		assertThat(savedGeneration.getPeriod().getStartDate()).isEqualTo(startDate);
		assertThat(savedGeneration.getPeriod().getEndDate()).isEqualTo(endDate);
		assertThat(savedGeneration.isVisible()).isEqualTo(true);
	}

	@Test
	void whenChangeGenerationPeriodWithOverlappingPeriod_thenThrowException() {
		// given
		Long generationId = 1L;
		LocalDate startDate = LocalDate.of(2025, 6, 1);
		LocalDate endDate = LocalDate.of(2025, 9, 1);
		Generation generation = Generation.builder()
			.number(3)
			.period(Period.of(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 5, 1)))
			.build();

		when(generationReader.findById(generationId)).thenReturn(generation);
		when(generationRepository.existsByPeriod_EndDateGreaterThanEqualAndPeriod_StartDateLessThanEqualAndIdNot(
			startDate, endDate, generationId))
			.thenReturn(true);

		// then: 예외 발생 확인
		assertThrows(AppException.class, () -> {
			generationService.changeGenerationPeriod(generationId, startDate, endDate);
		});
	}

	@Test
	void whenChangeGenerationPeriod_thenSuccess() {
		// given
		Long generationId = 1L;
		LocalDate startDate = LocalDate.of(2025, 6, 1);
		LocalDate endDate = LocalDate.of(2025, 9, 1);
		Generation generation = Generation.builder()
			.number(3)
			.period(Period.of(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 5, 1)))
			.build();

		when(generationReader.findById(generationId)).thenReturn(generation);
		when(generationRepository.existsByPeriod_EndDateGreaterThanEqualAndPeriod_StartDateLessThanEqualAndIdNot(
			startDate, endDate, generationId))
			.thenReturn(false);

		// when
		generationService.changeGenerationPeriod(generationId, startDate, endDate);

		// then
		assertThat(generation.getPeriod().getStartDate()).isEqualTo(startDate);
		assertThat(generation.getPeriod().getEndDate()).isEqualTo(endDate);
	}

	@Test
	void whenFindGenerations_thenReturnGenerations() {
		// given
		Generation generation1 = Generation.builder().number(3)
			.period(Period.of(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 5, 1)))
			.build();

		Generation generation2 = Generation.builder().number(3)
			.period(Period.of(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 5, 1)))
			.build();
		generation2.updateVisible(false);

		when(generationReader.getGenerations()).thenReturn(List.of(generation1));

		// when
		List<GenerationInfoResponse> generations = generationService.findGenerations();

		// then
		assertThat(generations).hasSize(1);
	}
}
