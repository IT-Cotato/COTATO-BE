package org.cotato.csquiz.common.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AWS SES 및 S3 관련 공통 설정을 관리하는 Properties 클래스
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {

    private String regionStatic;
    private Credentials credentials = new Credentials();
    private Ses ses = new Ses();


    @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Setter
    public static class Ses {
        private String emailAddress;
    }

    public String getAccessKey() {
        return credentials.accessKey;
    }

    public String getSecretKey() {
        return credentials.secretKey;
    }

    public String getEmailAddress() {
        return ses.emailAddress;
    }
}
