package org.cotato.csquiz.domain.generation.embedded;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.DevTalk;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
import org.hibernate.annotations.ColumnDefault;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionContents {

    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'IT_OFF'")
    private ItIssue itIssue;

    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'NW_OFF'")
    private Networking networking;

    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'CS_OFF'")
    private CSEducation csEducation;

    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'DEVTALK_OFF'")
    private DevTalk devTalk;

    public static SessionContents of(ItIssue itIssue, Networking networking, CSEducation csEducation, DevTalk devTalk) {
        return new SessionContents(itIssue, networking, csEducation, devTalk);
    }
}
