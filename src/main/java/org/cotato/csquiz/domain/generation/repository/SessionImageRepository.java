package org.cotato.csquiz.domain.generation.repository;

import java.util.List;

import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.entity.SessionImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionImageRepository extends JpaRepository<SessionImage, Long> {
	List<SessionImage> findAllBySession(Session session);

	List<SessionImage> findAllBySessionIn(List<Session> sessions);

	boolean existsBySessionAndOrder(Session session, Integer order);
}
