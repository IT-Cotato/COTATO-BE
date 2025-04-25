package org.cotato.csquiz.domain.recruitment.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import org.cotato.csquiz.domain.generation.embedded.Period;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentInformation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_opened", nullable = false)
    private Boolean isOpened;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate", column = @Column(name = "recruitment_start_date")),
            @AttributeOverride(name = "endDate", column = @Column(name = "recruitment_end_date"))
    })
    private Period period;

    @Column(name = "recruitment_url", columnDefinition = "TEXT")
    private String recruitmentUrl;

    @Builder
    public RecruitmentInformation(boolean isOpened, Period period, String recruitmentUrl) {
        this.isOpened = isOpened;
        this.period = period;
        this.recruitmentUrl = recruitmentUrl;
    }

    public LocalDate getEndDate() {
        if (period == null) {
            return null;
        }
        return period.getEndDate();
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void changeOpened(final boolean isOpened) {
        this.isOpened = isOpened;
    }

    public void changePeriod(final Period period) {
        this.period = period;
    }

    public void changeRecruitmentUrl(final String url) {
        this.recruitmentUrl = url;
    }
}
