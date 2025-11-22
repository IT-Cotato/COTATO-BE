package org.cotato.csquiz.common.config;

import org.cotato.csquiz.common.config.property.AwsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AwsSesConfig {

	private final AwsProperties awsProperties;

	@Bean
	public AmazonSimpleEmailService amazonSimpleEmailService() {
		final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(awsProperties.getAccessKey(),
			awsProperties.getSecretKey());
		final AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(
			basicAWSCredentials
		);

		return AmazonSimpleEmailServiceClientBuilder.standard()
			.withCredentials(awsStaticCredentialsProvider)
			.withRegion(awsProperties.getRegion())
			.build();
	}
}
