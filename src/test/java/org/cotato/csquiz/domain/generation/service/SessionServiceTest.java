package org.cotato.csquiz.domain.generation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.cotato.csquiz.api.attendance.dto.AttendanceDeadLineDto;
import org.cotato.csquiz.api.session.dto.SessionListImageInfoResponse;
import org.cotato.csquiz.api.session.dto.SessionListResponse;
import org.cotato.csquiz.api.session.dto.UpdateSessionRequest;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.event.CotatoEventPublisher;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.service.AttendanceService;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceReader;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceRecordReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.entity.SessionImage;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.DevTalk;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
import org.cotato.csquiz.domain.generation.enums.SessionType;
import org.cotato.csquiz.domain.generation.event.AttendanceEvent;
import org.cotato.csquiz.domain.generation.event.SessionImageEvent;
import org.cotato.csquiz.domain.generation.repository.AttendanceNotificationRepository;
import org.cotato.csquiz.domain.generation.repository.SessionImageRepository;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
import org.cotato.csquiz.domain.generation.service.dto.SessionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
	@Mock
	private SessionReader sessionReader;

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private AttendanceReader attendanceReader;

	@Mock
	private AttendanceRepository attendanceRepository;

	@Mock
	private AttendanceRecordReader attendanceRecordReader;

	@Mock
	private GenerationReader generationReader;

	@Mock
	private SessionImageRepository sessionImageRepository;

	@Mock
	private CotatoEventPublisher cotatoEventPublisher;

	@Mock
	private AttendanceService attendanceService;

	@Mock
	private AttendanceNotificationRepository attendanceNotificationRepository;

	@InjectMocks
	private SessionService sessionService;

	@Test
	@DisplayName("세션 생성 성공 테스트")
	void createSessionSuccess() {
		// given
		final Long generationId = 1L;
		final List<MultipartFile> images = List.of(mock(MultipartFile.class));
		final SessionDto sessionDto = SessionDto.builder().type(SessionType.ALL).build();
		final LocalDateTime attendanceDeadLine = LocalDateTime.now().plusDays(1);
		final LocalDateTime lateDeadline = LocalDateTime.now().plusDays(2);
		final Location location = Location.location(0.0, 0.0);

		Generation generation = mock(Generation.class);

		when(generationReader.findById(generationId)).thenReturn(generation);

		// when
		sessionService.addSession(generationId, images, sessionDto, attendanceDeadLine, lateDeadline, location);

		// then
		verify(sessionRepository).save(any());
		verify(cotatoEventPublisher).publishEvent(any(SessionImageEvent.class));
		verify(cotatoEventPublisher).publishEvent(any(AttendanceEvent.class));
	}

	@Test
	void whenUpdateSessionDateWithExistingAttendanceRecord_thenThrowException() {
		//given
		Long sessionId = 1L;
		LocalDateTime oldSessionDateTime = LocalDateTime.of(2025, 2, 1, 10, 0);
		LocalDateTime newSessionDateTime = LocalDateTime.of(2025, 2, 2, 10, 0); // 변경된 날짜

		UpdateSessionRequest request = mockOnlineUpdateSessionRequest(sessionId, newSessionDateTime);
		Session session = mockSession(sessionId, oldSessionDateTime);
		Attendance attendance = mockAttendance();

		when(sessionReader.findByIdWithPessimisticXLock(sessionId)).thenReturn(session);
		when(attendanceReader.findBySessionIdWithPessimisticXLock(sessionId)).thenReturn(Optional.of(attendance));
		when(attendanceRecordReader.isAttendanceRecordExist(attendance)).thenReturn(true);

		// when & then
		assertThrows(AppException.class, () -> sessionService.updateSession(request));
	}

	@Test
	void whenDeleteAttendanceWithExistingAttendanceRecord_thenThrowException() {
		//given
		Long sessionId = 1L;
		LocalDateTime sessionDateTime = LocalDateTime.of(2025, 2, 2, 10, 0); //변경X 날짜

		UpdateSessionRequest request = mockNoAttendUpdateSessionRequest(sessionId, sessionDateTime);
		Session session = mockSession(sessionId, sessionDateTime);
		Attendance attendance = mockAttendance();

		when(sessionReader.findByIdWithPessimisticXLock(sessionId)).thenReturn(session);
		when(attendanceReader.findBySessionIdWithPessimisticXLock(sessionId)).thenReturn(Optional.of(attendance));
		when(attendanceRecordReader.isAttendanceRecordExist(attendance)).thenReturn(true);

		// when & then
		assertThrows(AppException.class, () -> sessionService.updateSession(request));
	}

	@Test
	void whenNoAttendanceRecordExists_thenUpdatePossible() {
		//given
		final Long sessionId = 1L;
		LocalDateTime newSessionDateTime = LocalDateTime.of(2025, 2, 2, 10, 0); // 변경된 날짜

		UpdateSessionRequest request = mockOnlineUpdateSessionRequest(sessionId, newSessionDateTime);
		Session session = mock(Session.class);
		when(session.getId()).thenReturn(sessionId);
		Attendance attendance = mockAttendance();

		when(sessionReader.findByIdWithPessimisticXLock(sessionId)).thenReturn(session);
		when(attendanceReader.findBySessionIdWithPessimisticXLock(sessionId)).thenReturn(
			Optional.of(attendance)); // 출결 기록 없음
		when(attendanceRecordReader.isAttendanceRecordExist(attendance)).thenReturn(false);

		// when
		sessionService.updateSession(request);

		//then
		verify(sessionRepository).save(session);
		verify(attendanceRepository).save(Mockito.any(Attendance.class));
	}

	@Test
	void whenFindSessionsByGenerationId_thenSessionImagesAreSorted() {
		// given
		Long generationId = 1L;
		Generation generation = mock(Generation.class);
		Session session = mock(Session.class);
		when(generationReader.findById(generationId)).thenReturn(generation);
		when(sessionRepository.findAllByGeneration(generation)).thenReturn(List.of((session)));

		SessionImage image1 = SessionImage.builder()
			.session(session)
			.order(2)
			.s3Info(new S3Info("url2", "fileName", "folderName"))
			.build();
		SessionImage image2 = SessionImage.builder()
			.session(session)
			.order(1)
			.s3Info(new S3Info("url1", "fileName", "folderName"))
			.build();
		SessionImage image3 = SessionImage.builder()
			.session(session)
			.order(3)
			.s3Info(new S3Info("url3", "fileName", "folderName"))
			.build();

		when(sessionImageRepository.findAllBySessionIn(List.of(session)))
			.thenReturn(List.of(image1, image2, image3));
		when(generation.getId()).thenReturn(1L); // getId() 호출 시 1L 반환
		when(session.getGeneration()).thenReturn(generation);

		// when
		List<SessionListResponse> responses = sessionService.findSessionsByGenerationId(generationId);

		// then
		List<SessionListImageInfoResponse> images = responses.get(0).imageInfos();
		assertEquals(3, images.size());

		assertEquals(1, images.get(0).order());
		assertEquals(2, images.get(1).order());
		assertEquals(3, images.get(2).order());
	}

	@Test
	void findSessionsByGenerationIdSuccess() {
		// given
		Long generationId = 1L;
		Generation generation = mock(Generation.class);
		Session session = mock(Session.class);
		when(generation.getId()).thenReturn(generationId);
		when(session.getId()).thenReturn(1L);
		when(session.getGeneration()).thenReturn(generation);

		SessionImage image1 = SessionImage.builder()
			.session(session)
			.order(1)
			.s3Info(new S3Info("url1", "fileName1", "folderName1"))
			.build();
		SessionImage image2 = SessionImage.builder()
			.session(session)
			.order(2)
			.s3Info(new S3Info("url2", "fileName2", "folderName2"))
			.build();

		when(generationReader.findById(generationId)).thenReturn(generation);
		when(sessionRepository.findAllByGeneration(generation)).thenReturn(List.of(session));
		when(sessionImageRepository.findAllBySessionIn(List.of(session))).thenReturn(List.of(image1, image2));

		// when
		List<SessionListResponse> responses = sessionService.findSessionsByGenerationId(generationId);

		// then
		assertEquals(1, responses.size());
		assertEquals(2, responses.get(0).imageInfos().size());
		assertEquals(1, responses.get(0).imageInfos().get(0).order());
		assertEquals(2, responses.get(0).imageInfos().get(1).order());
	}

	@Test
	void whenSessionHasNoImages_thenReturnEmptyList() {
		// given
		Long generationId = 1L;
		Generation generation = mock(Generation.class);
		Session session = mock(Session.class);
		when(generationReader.findById(generationId)).thenReturn(generation);
		when(sessionRepository.findAllByGeneration(generation)).thenReturn(List.of(session));

		when(sessionImageRepository.findAllBySessionIn(List.of(session))).thenReturn(List.of());
		when(generation.getId()).thenReturn(1L); // getId() 호출 시 1L 반환
		when(session.getGeneration()).thenReturn(generation);

		// when
		List<SessionListResponse> responses = sessionService.findSessionsByGenerationId(generationId);

		// then
		assertEquals(1, responses.size());
		List<SessionListImageInfoResponse> images = responses.get(0).imageInfos();
		assertEquals(0, images.size());
		assertTrue(responses.get(0).imageInfos().isEmpty());
	}

	@Test
	@DisplayName("출석 삭제시 출석 알림도 함께 삭제되는지 테스트")
	void whenDeleteAttendance_thenDeleteAttendanceNotification() {
		// given
		Long sessionId = 1L;
		LocalDateTime sessionDateTime = LocalDateTime.of(2025, 2, 2, 10, 0);

		UpdateSessionRequest request = mockNoAttendUpdateSessionRequest(sessionId, sessionDateTime);
		Session session = mockSessionWithId(sessionId);
		Attendance attendance = mockAttendance();

		when(sessionReader.findByIdWithPessimisticXLock(sessionId)).thenReturn(session);
		when(attendanceReader.findBySessionIdWithPessimisticXLock(sessionId)).thenReturn(Optional.of(attendance));
		when(attendanceRecordReader.isAttendanceRecordExist(attendance)).thenReturn(false);

		// when
		sessionService.updateSession(request);

		// then
		verify(attendanceNotificationRepository).deleteByAttendance(attendance);
		verify(attendanceRepository).delete(attendance);
	}

	@Test
	@DisplayName("출석이 유지되는 경우 출석 알림은 삭제되지 않는지 테스트")
	void whenAttendanceIsKept_thenAttendanceNotificationIsNotDeleted() {
		// given
		Long sessionId = 1L;
		LocalDateTime sessionDateTime = LocalDateTime.of(2025, 2, 2, 10, 0);

		UpdateSessionRequest request = mockOnlineUpdateSessionRequest(sessionId, sessionDateTime);
		Session session = mockSessionWithId(sessionId);
		Attendance attendance = mockAttendance();

		when(sessionReader.findByIdWithPessimisticXLock(sessionId)).thenReturn(session);
		when(attendanceReader.findBySessionIdWithPessimisticXLock(sessionId)).thenReturn(Optional.of(attendance));
		when(attendanceRecordReader.isAttendanceRecordExist(attendance)).thenReturn(false);

		// when
		sessionService.updateSession(request);

		// then
		verify(attendanceNotificationRepository, Mockito.never()).deleteByAttendance(attendance);
		verify(attendanceRepository, Mockito.never()).delete(attendance);
		verify(attendanceRepository).save(any(Attendance.class));
	}

	private UpdateSessionRequest mockOnlineUpdateSessionRequest(Long sessionId, LocalDateTime sessionDateTime) {
		return new UpdateSessionRequest(sessionId, "New Title", "New Description",
			sessionDateTime, "New Place", "도로 명 주소",
			Location.location(0.0, 0.0),
			attendanceDeadLineDto(sessionDateTime.plusMinutes(1), sessionDateTime.plusMinutes(2)),
			true, false, ItIssue.IT_ON, Networking.NW_ON, CSEducation.CS_ON, DevTalk.DEVTALK_ON);
	}

	private UpdateSessionRequest mockNoAttendUpdateSessionRequest(Long sessionId, LocalDateTime sessionDateTime) {
		return new UpdateSessionRequest(sessionId, "New Title", "New Description",
			sessionDateTime, "New Place", "도로 명 주소",
			Location.location(0.0, 0.0),
			null, false, false, ItIssue.IT_ON, Networking.NW_ON, CSEducation.CS_ON, DevTalk.DEVTALK_ON);
	}

	private AttendanceDeadLineDto attendanceDeadLineDto(LocalDateTime attendanceDeadLine, LocalDateTime lateDeadLine) {
		return new AttendanceDeadLineDto(attendanceDeadLine, lateDeadLine);
	}

	private Session mockSession(Long sessionId, LocalDateTime sessionDateTime) {
		Session session = mock(Session.class);
		when(session.getId()).thenReturn(sessionId);
		when(session.getSessionDateTime()).thenReturn(sessionDateTime);
		return session;
	}

	private Session mockSessionWithId(Long sessionId) {
		Session session = mock(Session.class);
		when(session.getId()).thenReturn(sessionId);
		return session;
	}

	private Attendance mockAttendance() {
		return mock(Attendance.class);
	}
}
