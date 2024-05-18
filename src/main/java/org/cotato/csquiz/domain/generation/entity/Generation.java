package org.cotato.csquiz.domain.generation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.common.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Generation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "generation_id")
    private Long id;

    @Column(name = "generation_number", unique = true, nullable = false)
    private Integer number;

    @Column(name = "generation_session_count", nullable = false)
    private Integer sessionCount;

    @Column(name = "generation_start_date")
    private LocalDate startDate;

    @Column(name = "generation_end_date")
    private LocalDate endDate;

    @Column(name = "generation_recruiting")
    private Boolean isRecruit;

    @Builder
    public Generation(Integer number, Integer sessionCount, LocalDate startDate, LocalDate endDate) {
        this.number = number;
        this.sessionCount = sessionCount;
        this.startDate = startDate;
        this.endDate = endDate;
        isRecruit = false;
    }

    public void changeRecruit(Boolean isRecruit) {
        this.isRecruit = isRecruit;
    }

    public void changePeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
