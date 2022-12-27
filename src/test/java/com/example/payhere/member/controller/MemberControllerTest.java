package com.example.payhere.member.controller;

import com.example.payhere.member.controller.dto.LoginDto;
import com.example.payhere.member.controller.dto.SignupDto;
import com.example.payhere.member.domain.Member;
import com.example.payhere.member.repository.MemberRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Nested
@DisplayName("Member Controller 테스트")
class MemberControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    MemberRepository memberRepository;

    @Nested
    @DisplayName("회원가입 테스트(이메일, 닉네임, 패스워드)")
    class SignupTest {
        @Test
        @DisplayName("회원가입 성공")
        void signup() throws Exception {

            // 회원 가입을 위한 dto
            SignupDto dto = SignupDto.builder()
                    .email("signuptest@test.com")
                    .pw("test1234!")
                    .pwConfirm("test1234!")
                    .build();

            String json = new Gson().toJson(dto); // dto 를 json 형식의 String 으로 만들기

            // api 전송
            mvc.perform(post("/ph/signup")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                            .content(json))
                    .andExpect(status().isOk()) // 성공 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력

            Optional<Member> member = memberRepository.findByEmail("signuptest@test.com");

            memberRepository.delete(member.get());
        }
    }

    @Nested
    @DisplayName("로그인 테스트(이메일, 패스워드)")
    class LoginTest {
        @Test
        @Transactional
        @DisplayName("로그인 성공")
        void login() throws Exception {

            // 로그인을 위한 dto
            LoginDto loginDto = LoginDto.builder()
                    .email("user1@test.com")
                    .pw("asdf1234")
                    .build();

            // dto 를 json 형식의 String 으로 만들기
            String json = new Gson().toJson(loginDto);

            // api 전송
            mvc.perform(post("/ph/login")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                            .content(json))
                    .andExpect(status().isOk()) // 성공 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }
        @Nested
        @DisplayName("로그인 실패")
        class LoginFailed {

            @Test
            @DisplayName("이메일 유효성 없음")
            void notEmail() throws Exception {

                // 로그인을 위한 dto
                LoginDto dto = LoginDto.builder()
                        .email("failtest2@test.com")
                        .pw("pass1234^^")
                        .build();

                // dto 를 json 형식의 String 으로 만들기
                String json = new Gson().toJson(dto);

                // api 전송
                mvc.perform(post("/ph/login")// 요청 전송
                                .with(csrf()) // 403 에러를 방지하기 위한 csrf
                                .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                                .content(json))
                        .andExpect(status().isNonAuthoritativeInformation()) // 에러 코드 반환
                        .andDo(print()); // 요청과 응답 정보 전체 출력
            }

            @Test
            @DisplayName("비밀번호 유효성 없음")
            void notPw() throws Exception {

                // 이미 존재하는 이메일
                String existEmail = "user1@test.com";

                // 로그인을 위한 dto
                LoginDto dto = LoginDto.builder()
                        .email(existEmail)
                        .pw("failpw1234!")
                        .build();

                // dto 를 json 형식의 String 으로 만들기
                String json = new Gson().toJson(dto);

                // api 전송
                mvc.perform(post("/ph/login")// 요청 전송
                                .with(csrf()) // 403 에러를 방지하기 위한 csrf
                                .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                                .content(json))
                        .andExpect(status().isNonAuthoritativeInformation()) // 에러 코드 반환
                        .andDo(print()); // 요청과 응답 정보 전체 출력
            }
        }
    }

    @Test
    @WithUserDetails(value = "user1@test.com")
    @DisplayName("로그아웃 성공")
    void logout() throws Exception {

        // api 전송
        mvc.perform(post("/ph/logout")// 요청 전송
                        .with(csrf())) // 403 에러를 방지하기 위한 csrf
                .andExpect(status().isOk()) // 성공 코드 반환
                .andDo(print()); // 요청과 응답 정보 전체 출력
    }
}