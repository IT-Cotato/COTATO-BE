package org.cotato.csquiz.domain.generation.entity;

import static jakarta.persistence.FetchType.*;

import java.time.LocalDateTime;

import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.enums.SessionType;
import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "session_id")
	private Long id;

	@Column(name = "session_number")
	private Integer number;

	@Column(name = "session_title", length = 100)
	private String title;

	@Column(name = "session_description")
	private String description;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private SessionType sessionType;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "generation_id")
	private Generation generation;

	@AttributeOverrides({
		@AttributeOverride(name = "itIssue",
			column = @Column(name = "session_it_issue")),
		@AttributeOverride(name = "networking",
			column = @Column(name = "session_networking")),
		@AttributeOverride(name = "csEducation",
			column = @Column(name = "session_cs_education")),
		@AttributeOverride(name = "devTalk",
			column = @Column(name = "session_dev_talk"))
	})
	private SessionContents sessionContents;

	@Column(name = "session_start_time")
	private LocalDateTime sessionDateTime;

	@Column(name = "session_place_name")
	private String placeName;

	@Column(name = "road_name_address", columnDefinition = "TEXT")
	private String roadNameAddress;

	@Builder
	public Session(Integer number, String title, String description, String placeName, String roadNameAddress,
		LocalDateTime sessionDateTime,
		SessionType sessionType, Generation generation, SessionContents sessionContents) {
		this.number = number;
		this.title = title;
		this.description = description;
		this.placeName = placeName;
		this.roadNameAddress = roadNameAddress;
		this.sessionDateTime = sessionDateTime;
		this.sessionType = sessionType;
		this.generation = generation;
		this.sessionContents = sessionContents;
	}

	public void updateDescription(String description) {
		this.description = description;
	}

	public void updateSessionContents(SessionContents sessionContents) {
		this.sessionContents = sessionContents;
	}

	public void updateSessionTitle(String title) {
		this.title = title;
	}

	public void updateSessionDateTime(LocalDateTime sessionDateTime) {
		this.sessionDateTime = sessionDateTime;
	}

	public void updateSessionPlace(String placeName) {
		this.placeName = placeName;
	}

	public void updateRoadNameAddress(String roadNameAddress) {
		this.roadNameAddress = roadNameAddress;
	}

	public boolean hasOfflineSession() {
		return this.sessionType == SessionType.OFFLINE || this.sessionType == SessionType.ALL;
	}

	public void updateSessionType(SessionType sessionType) {
		this.sessionType = sessionType;
	}
}
