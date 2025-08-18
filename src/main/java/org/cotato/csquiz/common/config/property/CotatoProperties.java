package org.cotato.csquiz.common.config.property;

import java.util.List;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties(prefix = "cotato")
public class CotatoProperties {

    private List<String> baseUrl;

    public List<String> getBaseUrls() {
        return baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl.get(0);
    }
}
