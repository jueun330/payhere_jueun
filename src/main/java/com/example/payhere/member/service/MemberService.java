package com.example.payhere.member.service;

import com.example.payhere.member.controller.dto.LoginDto;
import com.example.payhere.member.controller.dto.SignupDto;
import com.example.payhere.member.domain.Member;
import com.example.payhere.member.repository.MemberRepository;
import com.example.payhere.shared.domain.PrivateResponseBody;
import com.example.payhere.shared.domain.StatusCode;
import com.example.payhere.shared.jwt.TokenDto;
import com.example.payhere.shared.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    //회원가입
    @Transactional
    public ResponseEntity<PrivateResponseBody> createMember(SignupDto requestDto) {

        // Email 중복 확인
        if (null != isPresentEmail(requestDto.getEmail())) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.DUPLICATED_EMAIL, null), HttpStatus.OK);
        }

        // 비밀번호 확인
        if (!requestDto.getPw().equals(requestDto.getPwConfirm())) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.DUPLICATED_PASSWORD, null), HttpStatus.OK);
        }

        // 회원 정보 저장
        Member member = Member.builder()
                .email(requestDto.getEmail())
                .pw(passwordEncoder.encode(requestDto.getPw()))
                .build();
        memberRepository.save(member);

        // Message 및 Status를 Return
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, "회원가입 성공"), HttpStatus.OK);
    }

    //로그인
    @Transactional
    public ResponseEntity<PrivateResponseBody> login(LoginDto requestDto, HttpServletResponse response) {
        Member member = isPresentEmail(requestDto.getEmail());

        // DB에 Email 확인
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.LOGIN_EMAIL_FAIL, null), HttpStatus.OK);
        }

        // DB에 PW 확인
        if (!member.validatePassword(passwordEncoder, requestDto.getPw())) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.LOGIN_PASSWORD_FAIL, null), HttpStatus.OK);
        }

        //토큰 지급
        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenToHeaders(tokenDto, response);

        // Message 및 Status를 Return
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, "로그인 성공"), HttpStatus.OK);
    }

    //로그아웃
    public ResponseEntity<PrivateResponseBody> logout(HttpServletRequest request) {

        // 토큰 확인
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.LOGIN_WRONG_SIGNATURE_JWT_TOKEN, null), HttpStatus.OK);
        }
        Member member = tokenProvider.getMemberFromAuthentication();

        // 회원 확인
        if (null == member) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.LOGIN_EMAIL_FAIL, null), HttpStatus.OK);
        }

        tokenProvider.deleteRefreshToken(member);

        // Message 및 Status를 Return
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, "로그아웃 완료"), HttpStatus.OK);
    }

    //Email 확인
    @Transactional(readOnly = true)
    public Member isPresentEmail(String email) {
        Optional<Member> optionalEmail = memberRepository.findByEmail(email);
        return optionalEmail.orElse(null);
    }
    //토큰 지급
    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }
}

