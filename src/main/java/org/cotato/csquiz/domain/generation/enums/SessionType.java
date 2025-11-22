package org.cotato.csquiz.domain.generation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;

@Getter
@RequiredArgsConstructor
public enum SessionType {
	NO_ATTEND("출석을 진행하지 않는 세션", false),
	ONLINE("비대면으로만 진행하는 세션", true),
	OFFLINE("대면으로만 진행하는 세션", true),
	ALL("대면, 비대면 혼용", true);

	private final String description;
	private final boolean createAttendance;

	public static SessionType getSessionType(boolean isOffline, boolean isOnline) {
		if (isOffline && isOnline) {
			return ALL;
		}
		if (isOnline) {
			return ONLINE;
		}
		if (isOffline) {
			return OFFLINE;
		}
		return NO_ATTEND;
	}

	public boolean isSameType(AttendanceType attendanceType) {
		if (attendanceType == null) {
			return false;
		}

		return switch (attendanceType) {
			case OFFLINE -> this == OFFLINE || this == ALL;
			case ONLINE -> this == ONLINE || this == ALL;
			default -> false;
		};
	}

	public boolean hasOffline() {
		return this == OFFLINE || this == ALL;
	}

	public boolean hasOnline() {
		return this == ONLINE || this == ALL;
	}

	public boolean canChangeResult(AttendanceResult attendanceResult) {
		if (attendanceResult == null) {
			return false;
		}

		return switch (attendanceResult) {
			case ONLINE -> this == ONLINE || this == ALL;
			case OFFLINE -> this == OFFLINE || this == ALL;
			case ABSENT, LATE -> this != NO_ATTEND;
		};
	}
}
