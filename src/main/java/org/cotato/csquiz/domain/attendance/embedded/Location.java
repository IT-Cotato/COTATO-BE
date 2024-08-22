package org.cotato.csquiz.domain.attendance.embedded;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    private Double latitude;
    private Double longitude;
    
    @Builder
    private Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double calculateAccuracy(Location location) {
        return Math.pow(this.latitude - location.latitude, 2) + Math.pow(this.longitude - location.longitude, 2);
    }
}
