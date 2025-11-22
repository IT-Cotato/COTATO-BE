package org.cotato.csquiz.common.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {

	private String region;
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
