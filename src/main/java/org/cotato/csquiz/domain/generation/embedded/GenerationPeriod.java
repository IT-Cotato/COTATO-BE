package org.cotato.csquiz.domain.generation.embedded;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GenerationPeriod {
    private LocalDate startDate;
    private LocalDate endDate;

    private GenerationPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static GenerationPeriod of(LocalDate startDate, LocalDate endDate) {
        return new GenerationPeriod(startDate, endDate);
    }
}
