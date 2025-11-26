package org.cotato.csquiz.domain.generation.entity;

import static jakarta.persistence.FetchType.*;

import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.common.entity.S3Info;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionImage extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "session_image_id")
	private Long id;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "folderName", column = @Column(nullable = false)),
		@AttributeOverride(name = "fileName", column = @Column(nullable = false)),
		@AttributeOverride(name = "url", column = @Column(nullable = false))
	})
	private S3Info s3Info;

	@Column(name = "session_image_order", nullable = false)
	private Integer order;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "session_id")
	private Session session;

	@Builder
	public SessionImage(Session session, Integer order, S3Info s3Info) {
		this.session = session;
		this.order = order;
		this.s3Info = s3Info;
	}

	public void updateOrder(Integer order) {
		this.order = order;
	}

	public void decreaseOrder() {
		if (order > 0) {
			order--;
		}
	}
}
