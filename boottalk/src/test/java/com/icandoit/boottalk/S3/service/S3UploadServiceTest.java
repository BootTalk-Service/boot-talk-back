package com.icandoit.boottalk.S3.service;

import static com.icandoit.boottalk.libs.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.icandoit.boottalk.libs.exception.CustomException;

import software.amazon.awssdk.services.s3.S3Client;

class S3UploadServiceTest {
	@Mock
	private S3Client s3Client;

	@InjectMocks
	private S3UploadService s3UploadService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		// Bucket name를 모의 값으로 설정
		ReflectionTestUtils.setField(s3UploadService, "bucketName", "boottalk-bucket");
	}

	@Test
	@DisplayName("업로드 살패 - 빈 파일 업로드")
	public void uploadFileFailedEmptyFile() {
		// given
		MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			s3UploadService.uploadFile(emptyFile);
		});

		assertEquals(FILE_IS_EMPTY, exception.getErrorCode());
	}

	@Test
	@DisplayName("업로드 실패 - 잘못된 이미지 타입")
	public void uploadFileFailedInvalidImageType() {
		// given
		MockMultipartFile textFile = new MockMultipartFile("file", "test.txt", "text/plain",
			"dummy-content".getBytes());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			s3UploadService.uploadFile(textFile);
		});
		assertEquals(INVALID_IMAGE_TYPE, exception.getErrorCode(), "이미지 파일이 아닌 경우 에러가 발생해야 합니다.");
	}

	@Test
	@DisplayName("업로드 실패 - 잘못된 파일명 업로드")
	public void uploadFileFailedInvalidFileName() {
		// given
		// 파일명에 ".." 포함하여 경로 조작을 시도하는 파일 생성
		MockMultipartFile maliciousFile = new MockMultipartFile("file", "../img.jpg", "image/jpeg",
			"dummy-content".getBytes());

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			s3UploadService.uploadFile(maliciousFile);
		});

		assertEquals(INVALID_FILE_NAME, exception.getErrorCode(), "잘못된 파일명 경우 에러가 발생해야 합니다.");
	}
}