package com.icandoit.boottalk.coffeeChat.controller;

import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoResponseDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatInfoRequestDto;
import com.icandoit.boottalk.coffeeChat.dto.CoffeeChatSearchRequestDto;
import com.icandoit.boottalk.coffeeChat.service.CoffeeChatInfoService;
import com.icandoit.boottalk.libs.dto.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coffee-chats-info")
@RequiredArgsConstructor
public class CoffeeChatInfoController {
    private final CoffeeChatInfoService coffeeChatInfoService;

    // 커피챗 등록
    @PostMapping()
    public ResponseEntity<SuccessResponseDto<CoffeeChatInfoResponseDto>> createCoffeeChatInfo(
        @RequestBody CoffeeChatInfoRequestDto requestDto
    ) {
        return ResponseEntity.ok(coffeeChatInfoService.createCoffeeChatInfo(requestDto));
    }

    //내 커피챗 정보조회
    // todo: Authorization -> 서비스에 유저 정보 넘기기
    @GetMapping("/me")
    public ResponseEntity<SuccessResponseDto<CoffeeChatInfoResponseDto>> getMyCoffeeChatInfo() {
        return ResponseEntity.ok(coffeeChatInfoService.getMyCoffeeChatInfo(userId));
    }

    // 커피챗 목록 검색 (전체)
    // todo : 추후 querydsl 적용하여 진행하겠습니당..
//    @GetMapping("/search")
//    public ResponseEntity<SuccessResponseDto<Page<CoffeeChatInfoResponseDto>>> searchCoffeeChatInfo(
//        CoffeeChatSearchRequestDto searchDto
//    ) {
//        CoffeeChatSearchRequestDto processedDto = CoffeeChatSearchRequestDto.from(searchDto);
//
//        SuccessResponseDto<Page<CoffeeChatInfoResponseDto>> response =
//            coffeeChatInfoService.searchCoffeeChatInfo(processedDto);
//        return ResponseEntity.ok(response);
//    }

    // 내 커피챗 수정
    // todo : 인증 추가 -> service 넘기기
    // todo : 유효성 검사 추가
    @PutMapping("/me")
    public ResponseEntity<SuccessResponseDto<CoffeeChatInfoResponseDto>> updateCoffeeChatInfo(
        @RequestBody CoffeeChatInfoRequestDto requestDto)
    {
        return ResponseEntity.ok(coffeeChatInfoService.updateMyCoffeeChatInfo(userId, requestDto));
    }

    // 내 커피챗 삭제
    // todo : 인증 추가 -> service 넘기기
    @DeleteMapping("/me")
    public ResponseEntity<SuccessResponseDto<CoffeeChatInfoResponseDto>> deleteCoffeeChatInfo(){
        return ResponseEntity.ok(coffeeChatInfoService.deleteMyCoffeeChatInfo(userId));
    }
}
