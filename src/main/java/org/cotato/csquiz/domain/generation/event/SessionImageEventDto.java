package org.cotato.csquiz.domain.generation.event;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.cotato.csquiz.domain.generation.entity.Session;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionImageEventDto {

	private Session session;

	private List<MultipartFile> images;
}
