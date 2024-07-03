package org.cotato.csquiz.domain.generation.entity;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.api.session.dto.SessionListPhotoInfoResponse;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.common.entity.S3Info;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionPhoto extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_image_id")
    private Long id;

    @Embedded
    private S3Info s3Info;

    @Column(name = "session_image_order")
    private Integer order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    @Builder
    public SessionPhoto(Session session, Integer order, S3Info s3Info) {
        this.session = session;
        this.order = order;
        this.s3Info = s3Info;
    }

    public void updateOrder(Integer order) {
        this.order = order;
    }

    public void decreaseOrder() {
        this.order--;
    }
}
