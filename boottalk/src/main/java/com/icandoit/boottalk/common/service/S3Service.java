package com.icandoit.boottalk.common.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@RequiredArgsConstructor
public class S3Service {
	private final S3Client s3Client;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	// 파일을 S3에 업로드하고 URL 반환
	public String uploadFile(MultipartFile file, String folderName) throws IOException {
		String fileName = folderName + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
		File tempFile = convertMultiPartToFile(file);

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(fileName)
			.build();

		s3Client.putObject(putObjectRequest, tempFile.toPath());
		tempFile.delete();

		return generateFileUrl(fileName);
	}

	// S3 파일 URL 생성
	public String generateFileUrl(String fileName) {
		return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
	}

	// MultipartFile을 File로 변환
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File tempFile = new File(file.getOriginalFilename());
		file.transferTo(tempFile);
		return tempFile;
	}

	// 파일 다운로드
	public S3Object downloadFile(String fileName) {
		return s3Client.getObject(builder -> builder.bucket(bucketName).key(fileName));
	}
}
