package com.example.payhere.member.controller;

import com.example.payhere.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ph")
public class MemberController {

    private final MemberService memberService;


}
