package cotato.csquiz.config.jwt;

import jakarta.persistence.Id;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@RedisHash(value = "blackList")
public class BlackList {

    @Id
    private String id;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long ttl;
}
