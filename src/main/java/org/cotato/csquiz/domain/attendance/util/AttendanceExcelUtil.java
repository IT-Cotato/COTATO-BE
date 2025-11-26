package org.cotato.csquiz.domain.attendance.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;

public class AttendanceExcelUtil {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년MM월dd일");

	public static String getGenerationRecordExcelFileName(final Generation generation) {
		LocalDateTime now = LocalDateTime.now();
		return String.format("%s기-출석-현황-%s기준", generation.getNumber(), now.format(FORMATTER));
	}

	public static String getAttendanceRecordExcelFileName(final List<Attendance> attendances,
		final Map<Long, Session> sessionById,
		final Generation generation) {
		LocalDateTime now = LocalDateTime.now();
		return String.format("%s기-%s-출석-현황-%s기준", generation.getNumber(), getSessionNames(attendances, sessionById),
			now.format(FORMATTER));
	}

	private static String getSessionNames(List<Attendance> attendances, Map<Long, Session> sessionById) {
		return attendances.stream()
			.map(attendance -> sessionById.get(attendance.getSessionId()))
			.map(Session::getNumber)
			.map(String::valueOf)
			.collect(Collectors.joining(", "));
	}
}
