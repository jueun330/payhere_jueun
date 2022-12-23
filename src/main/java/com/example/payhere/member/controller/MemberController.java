package com.example.payhere.member.controller;

import com.example.payhere.member.controller.dto.LoginDto;
import com.example.payhere.member.controller.dto.SignupDto;
import com.example.payhere.member.service.MemberService;
import com.example.payhere.shared.domain.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ph")
public class MemberController {

    private final MemberService memberService;

    //회원 가입
    @PostMapping(value = "/signup")
    public ResponseEntity<PrivateResponseBody> signup(@RequestBody SignupDto requestDto) {
        return memberService.createMember(requestDto);
    }

    //로그인
    @PostMapping(value = "/login")
    public ResponseEntity<PrivateResponseBody> login(@RequestBody LoginDto requestDto,
                                                     HttpServletResponse response) {
        return memberService.login(requestDto, response);
    }

    //로그아웃
    @PostMapping(value = "/logout")
    public ResponseEntity<PrivateResponseBody> logout(HttpServletRequest request) {
        return memberService.logout(request);
    }
}
