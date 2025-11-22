package org.cotato.csquiz.domain.generation.event;

import java.util.List;

import org.cotato.csquiz.domain.generation.entity.Session;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionImageEventDto {

	private Session session;

	private List<MultipartFile> images;
}
