package com.icandoit.boottalk.S3.service;

import static com.icandoit.boottalk.libs.exception.ErrorCode.*;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.icandoit.boottalk.libs.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:s3-key.properties")
public class S3UploadService {

	private final S3Client s3Client;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	// 파일 검증 및 S3 버킷에 업로드하고 파일 URL 을 반환하는 메소드
	public String uploadFile(MultipartFile file) {

		if (file == null || file.isEmpty()) {
			throw new CustomException(FILE_IS_EMPTY);
		}

		// MIME 타입 검증 - 이미지 파일만 허용 (예 : image/jpeg, image/png 등)
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new CustomException(INVALID_IMAGE_TYPE);
		}

		// 파일명 보정
		String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
		// 경로를 설정 하는것을 방지
		if (originalFilename.contains("..")) {
			throw new CustomException(INVALID_FILE_NAME);
		}

		try {
			// 파일을 임시파일로 변환
			File tempFile = convertMultipartFileToFile(file);
			// S3에 저장할 경로 구성
			String fileName = "uploads/" + System.currentTimeMillis() + "_" + originalFilename;

			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(fileName)
				.build();

			s3Client.putObject(putObjectRequest, tempFile.toPath());
			// 임시 파일 삭제
			tempFile.delete();

			return generateFileUrl(fileName);
		} catch (IOException e) {
			log.error("파일 업로드 실패", e);
			throw new CustomException(FILE_UPLOAD_FAILED);
		}
	}

	// S3에 업로드한 파일의 URL 생성
	private String generateFileUrl(String fileName) {
		return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
	}

	// MultipartFile을 File 객체로 변환
	private File convertMultipartFileToFile(MultipartFile file) throws IOException {
		// 임시 파일을 생성. 접두사와 접미사는 원본 파일명으로부터 결정하거나 고정 문자열로 사용할 수 있습니다.
		String prefix = StringUtils.getFilename(file.getOriginalFilename());
		String suffix = "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
		// 접두사가 3글자 이상이어야 하므로, 필요시 고정 값 추가
		if(prefix == null || prefix.length() < 3) {
			prefix = "upload";
		}
		File convFile = File.createTempFile(prefix, suffix);
		// MultipartFile 의 데이터를 임시 파일로 저장
		file.transferTo(convFile);
		return convFile;
	}
}
