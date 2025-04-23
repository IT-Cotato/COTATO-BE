package org.cotato.csquiz.domain.education.service.component;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.enums.EducationStatus;
import org.cotato.csquiz.domain.education.repository.EducationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EducationReader {

    private final EducationRepository educationRepository;

    public Education getById(final Long id) {
        return educationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 교육이 존재하지 않습니다."));
    }

    public List<Education> getAllByStatus(EducationStatus educationStatus) {
        return educationRepository.findAllByStatus(educationStatus);
    }
}
