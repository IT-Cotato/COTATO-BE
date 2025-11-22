package org.cotato.csquiz.common.config.jwt;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@RedisHash(value = "blackList")
public class BlackList {

	@Id
	private String id;

	@TimeToLive(unit = TimeUnit.MILLISECONDS)
	private Long ttl;
}
