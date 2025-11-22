package org.cotato.csquiz.domain.auth.service.component;

import java.util.List;

import org.cotato.csquiz.domain.auth.entity.Policy;
import org.cotato.csquiz.domain.auth.enums.PolicyCategory;
import org.cotato.csquiz.domain.auth.repository.PolicyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PolicyReader {

	private final PolicyRepository policyRepository;

	public List<Policy> getPoliciesByCategory(PolicyCategory category) {
		return policyRepository.findAllByCategory(category);
	}
}
