package org.cotato.csquiz.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtil {

	private static final String SEOUL_ZONE = "Asia/Seoul";

	public static ZonedDateTime getSeoulZoneTime(LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.of(SEOUL_ZONE));
	}
}
