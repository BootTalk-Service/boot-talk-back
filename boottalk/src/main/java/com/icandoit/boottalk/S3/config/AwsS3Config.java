package com.icandoit.boottalk.S3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@PropertySource("classpath:s3-key.properties")
public class AwsS3Config {

	@Value("${aws.s3.access-key}")
	private String accessKey;

	@Value("${aws.s3.secret-key}")
	private String secretKey;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	@Bean
	public S3Client s3Client() {
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
		return S3Client.builder()
			.credentialsProvider(StaticCredentialsProvider.create(awsCreds))
			.region(Region.AP_NORTHEAST_2)  // 서울 리전으로 설정
			.build();
	}
}
