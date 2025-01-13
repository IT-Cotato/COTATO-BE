package org.cotato.csquiz.domain.generation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.cotato.csquiz.api.generation.dto.AddGenerationRequest;
import org.cotato.csquiz.api.generation.dto.AddGenerationResponse;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(GenerationService.class)
class GenerationServiceIntegrationTest {

    @Autowired
    private GenerationService generationService;

    @Autowired
    private GenerationRepository generationRepository;

    @BeforeEach
    void setUp() {
        generationRepository.deleteAll();  // 이전에 저장된 데이터 모두 삭제
    }

    @Test
    void 기수_추가_통합테스트() {
        //given
        AddGenerationRequest request = new AddGenerationRequest(1, LocalDate.now(), LocalDate.now().plusMonths(1), 12);

        //when
        AddGenerationResponse response = generationService.addGeneration(request);

        //then
        Generation generation = generationRepository.findById(response.generationId()).orElseThrow();
        assertThat(response.generationId()).isEqualTo(generation.getId());
    }
}
