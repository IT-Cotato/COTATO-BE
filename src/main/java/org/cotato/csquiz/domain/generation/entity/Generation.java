package org.cotato.csquiz.domain.generation.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.generation.embedded.Period;

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

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "startDate", column = @Column(name = "generation_start_date")),
		@AttributeOverride(name = "endDate", column = @Column(name = "generation_end_date"))
	})
	private Period period;

	@Column(name = "generation_recruiting")
	private Boolean isRecruit;

	@Column(name = "visible")
	private boolean visible;

	@Builder
	public Generation(Integer number, Period period) {
		this.number = number;
		this.period = period;
		this.isRecruit = false;
		this.visible = true;
	}

	public void changeRecruit(Boolean isRecruit) {
		this.isRecruit = isRecruit;
	}

	public void changePeriod(Period period) {
		this.period = period;
	}

	public void updateVisible(boolean visible) {
		this.visible = visible;
	}
}
