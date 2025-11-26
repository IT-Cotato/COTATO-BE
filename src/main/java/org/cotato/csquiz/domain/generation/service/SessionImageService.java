package org.cotato.csquiz.domain.generation.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.cotato.csquiz.api.session.dto.AddSessionImageRequest;
import org.cotato.csquiz.api.session.dto.AddSessionImageResponse;
import org.cotato.csquiz.api.session.dto.DeleteSessionImageRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionImageOrderInfoRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionImageOrderRequest;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.s3.S3Uploader;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.entity.SessionImage;
import org.cotato.csquiz.domain.generation.repository.SessionImageRepository;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SessionImageService {
	private static final String SESSION_BUCKET_DIRECTORY = "session";
	private final SessionImageRepository sessionImageRepository;
	private final SessionRepository sessionRepository;
	private final S3Uploader s3Uploader;

	@Transactional(rollbackFor = ImageException.class)
	public void addSessionImages(List<MultipartFile> images, Session session) throws ImageException {
		if (images == null || images.isEmpty()) {
			log.info("세션 이미지가 없습니다. 세션 ID: {}", session.getId());
			return;
		}

		AtomicInteger index = new AtomicInteger(0);

		List<SessionImage> sessionImages = new ArrayList<>();

		for (MultipartFile imageFile : images) {
			S3Info s3Info = s3Uploader.uploadFiles(imageFile, SESSION_BUCKET_DIRECTORY);

			SessionImage sessionImage = SessionImage.builder()
				.session(session)
				.s3Info(s3Info)
				.order(index.getAndIncrement())
				.build();

			sessionImages.add(sessionImage);
		}

		sessionImageRepository.saveAll(sessionImages);
		log.info("세션 이미지 생성 완료");
	}

	@Transactional
	public AddSessionImageResponse additionalSessionImage(AddSessionImageRequest request) throws ImageException {
		Session session = findSessionById(request.sessionId());

		S3Info imageInfo = s3Uploader.uploadFiles(request.image(), SESSION_BUCKET_DIRECTORY);

		if (sessionImageRepository.existsBySessionAndOrder(session, request.order())) {
			throw new AppException(ErrorCode.SESSION_ORDER_INVALID);
		}

		SessionImage sessionImage = SessionImage.builder()
			.session(session)
			.s3Info(imageInfo)
			.order(request.order())
			.build();

		return AddSessionImageResponse.from(sessionImageRepository.save(sessionImage));
	}

	@Transactional
	public void deleteSessionImage(DeleteSessionImageRequest request) {
		SessionImage deleteImage = sessionImageRepository.findById(request.imageId())
			.orElseThrow(() -> new EntityNotFoundException("해당 사진을 찾을 수 없습니다."));
		s3Uploader.deleteFile(deleteImage.getS3Info());
		sessionImageRepository.delete(deleteImage);

		List<SessionImage> reorderImages = sessionImageRepository.findAllBySession(deleteImage.getSession()).stream()
			.filter(image -> image.getOrder() > deleteImage.getOrder())
			.toList();

		for (SessionImage sessionImage : reorderImages) {
			sessionImage.decreaseOrder();
		}
	}

	@Transactional
	public void updateSessionImageOrder(UpdateSessionImageOrderRequest request) {
		Session sessionById = findSessionById(request.sessionId());
		List<UpdateSessionImageOrderInfoRequest> orderList = request.orderInfos();

		List<SessionImage> savedImages = sessionImageRepository.findAllBySession(sessionById);

		if (savedImages.size() != orderList.size()) {
			throw new AppException(ErrorCode.SESSION_IMAGE_COUNT_MISMATCH);
		}

		if (!isValidOrderRange(orderList)) {
			throw new AppException(ErrorCode.SESSION_ORDER_INVALID);
		}

		if (!isOrderUnique(orderList)) {
			throw new AppException(ErrorCode.SESSION_ORDER_INVALID);
		}

		Map<Long, UpdateSessionImageOrderInfoRequest> orderMap = orderList.stream()
			.collect(Collectors.toMap(UpdateSessionImageOrderInfoRequest::imageId, Function.identity()));

		for (SessionImage savedImage : savedImages) {
			if (orderMap.get(savedImage.getId()) == null) {
				throw new EntityNotFoundException("해당 사진을 찾을 수 없습니다.");
			}
			savedImage.updateOrder(orderMap.get(savedImage.getId()).order());
		}
	}

	private boolean isValidOrderRange(List<UpdateSessionImageOrderInfoRequest> orderList) {
		return orderList.stream().noneMatch(orderInfo ->
			orderInfo.order() < 0 || orderInfo.order() >= orderList.size());
	}

	private boolean isOrderUnique(List<UpdateSessionImageOrderInfoRequest> orderList) {
		Set<Integer> uniqueOrders = new HashSet<>();
		for (UpdateSessionImageOrderInfoRequest orderInfo : orderList) {
			if (!uniqueOrders.add(orderInfo.order())) {
				return false;
			}
		}

		return true;
	}

	public Session findSessionById(Long sessionId) {
		return sessionRepository.findById(sessionId)
			.orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
	}
}
