package com.icandoit.boottalk.S3.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.icandoit.boottalk.S3.dto.FileUploadResponseDto;
import com.icandoit.boottalk.S3.service.S3UploadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileUploadController {

	private final S3UploadService s3UploadService;

	@PostMapping("/upload")
	public ResponseEntity<FileUploadResponseDto> uploadFile(@RequestPart("file") MultipartFile file) {
		String url = s3UploadService.uploadFile(file);
		return ResponseEntity.ok(new FileUploadResponseDto(url));
	}
}
