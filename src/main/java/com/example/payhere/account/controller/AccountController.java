package com.example.payhere.account.controller;

import com.example.payhere.account.controller.dto.AccountRequestDto;
import com.example.payhere.account.service.AccountService;
import com.example.payhere.shared.domain.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ph")
public class AccountController {

    private final AccountService accountService;


    // 가계부 작성
    @ResponseBody
    @PostMapping(value = "/accounts")
    public ResponseEntity<PrivateResponseBody> createAccount(@RequestBody AccountRequestDto requestDto, @AuthenticationPrincipal Long memberId) {
        return accountService.createAccount(requestDto, memberId);
    }

    // 가계부 수정
    @ResponseBody
    @PutMapping(value = "/accounts/{accountId}")
    public ResponseEntity<PrivateResponseBody> updateAccount(@RequestBody AccountRequestDto requestDto, // 수정 사항
                                                             @PathVariable Long accountId, // 수정하고자 하는 가계부의 고유 ID
                                                             HttpServletRequest request){
        return accountService.updateAccount(requestDto, accountId, request);
    }

    // 가계부 삭제
    @ResponseBody
    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<PrivateResponseBody> deleteAccount(@PathVariable Long accountId, HttpServletRequest request){
        return accountService.deleteAccount(request, accountId);
    }

    // 가계부 복구
    @ResponseBody
    @GetMapping("/accounts/restore/{accountId}")
    public ResponseEntity<PrivateResponseBody> restoreAccount(@PathVariable Long accountId, HttpServletRequest request){
        return accountService.restoreDeleted(request, accountId);
    }

    // 가계부 목록
    @ResponseBody
    @GetMapping("/accounts")
    public ResponseEntity<PrivateResponseBody> accountList(HttpServletRequest request){
        return accountService.accountList(request);
    }

    // 가계부 세부 목록
    @ResponseBody
    @GetMapping("/accounts/details")
    public ResponseEntity<PrivateResponseBody> detailList(HttpServletRequest request){
        return accountService.accountDetail(request);
    }
}
