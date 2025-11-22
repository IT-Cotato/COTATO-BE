package org.cotato.csquiz.domain.generation.entity;

import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.generation.enums.GenerationMemberRole;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GenerationMember extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "generation_member_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "generation_id", nullable = false)
	private Generation generation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	@ColumnDefault(value = "'MEMBER'")
	private GenerationMemberRole role = GenerationMemberRole.MEMBER;

	private GenerationMember(Generation generation, Member member, GenerationMemberRole role) {
		this.generation = generation;
		this.member = member;
		this.role = role;
	}

	public static GenerationMember of(Generation generation, Member member) {
		return new GenerationMember(generation, member, GenerationMemberRole.MEMBER);
	}

	public void updateMemberRole(GenerationMemberRole role) {
		this.role = role;
	}

	public String getMemberName() {
		return member.getName();
	}
}
