package org.cotato.csquiz.domain.generation.service.component;

import io.netty.resolver.dns.DnsServerAddresses;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenerationReader {

    private final GenerationRepository generationRepository;

    public Generation findByDate(LocalDate date) {
        return generationRepository.findByCurrentDate(date)
                .orElseThrow(() -> new EntityNotFoundException(date + "해당 날짜에 해당하는 기수를 찾을 수 없습니다."));
    }

    public Generation findById(Long generationId) {
        return generationRepository.findById(generationId).orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));
    }

    public List<Generation> getGenerations() {
        return generationRepository.findAllByVisibleTrue();
    }
}
