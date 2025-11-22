package org.cotato.csquiz.migration;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MigrationLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "migration_name", unique = true)
	private String name;

	private LocalDateTime createdAt;

	@Builder
	public MigrationLog(String name, LocalDateTime createdAt) {
		this.name = name;
		this.createdAt = createdAt;
	}
}
