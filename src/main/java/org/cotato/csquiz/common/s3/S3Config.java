package org.cotato.csquiz.common.s3;

import org.cotato.csquiz.common.config.property.AwsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class S3Config {

	private final AwsProperties awsProperties;

	@Bean
	public AmazonS3Client amazonS3Client() {
		BasicAWSCredentials credentials = new BasicAWSCredentials(awsProperties.getAccessKey(),
			awsProperties.getSecretKey());
		return (AmazonS3Client)AmazonS3ClientBuilder.standard()
			.withRegion(awsProperties.getRegion())
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.build();
	}
}
