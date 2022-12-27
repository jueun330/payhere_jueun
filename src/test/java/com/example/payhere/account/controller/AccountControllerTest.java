package com.example.payhere.account.controller;

import com.example.payhere.account.controller.dto.AccountRequestDto;
import com.example.payhere.account.repository.AccountRepository;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails(value = "user1@test.com")
@Nested
@DisplayName("Account Controller 테스트")
class AccountControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    AccountRepository accountRepository;

    @Nested
    @DisplayName("가계부 작성 테스트(금액, 메모)")
    class SignupTest {
        @Test
        @DisplayName("작성 성공")
        void createAccount() throws Exception {

            // 가계부 작성을 위한 dto
            AccountRequestDto dto = AccountRequestDto.builder()
                    .money(1000)
                    .memo("testMemo")
                    .build();

            String json = new Gson().toJson(dto); // dto 를 json 형식의 String 으로 만들기

            // api 전송
            mvc.perform(post("/ph/accounts")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                            .content(json))
                    .andExpect(status().isOk()) // 성공 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }
        @Test
        @DisplayName("작성 실패-금액 누락")
        void createAccount_fail() throws Exception {

            // 가계부 작성을 위한 dto
            AccountRequestDto dto = AccountRequestDto.builder()
                    .memo("testMemo")
                    .build();

            String json = new Gson().toJson(dto); // dto 를 json 형식의 String 으로 만들기

            // api 전송
            mvc.perform(post("/ph/accounts")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                            .content(json))
                    .andExpect(status().is4xxClientError()) // 실패 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }
    }

    @Nested
    @DisplayName("가계부 수정 테스트(금액, 메모)")
    class UpdateTest {
        @DisplayName("수정 성공")
        @Test
        void updateAccount() {

            // 가계부 수정을 위한 dto
            AccountRequestDto dto = AccountRequestDto.builder()
                    .money(1000)
                    .memo("testMemo")
                    .build();

            String json = new Gson().toJson(dto); // dto 를 json 형식의 String 으로 만들기

            // api 전송
            mvc.perform(put("/ph/accounts/1")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON)// json 형식으로 데이터를 보낸다고 명시
                            .content(json))
                    .andExpect(status().isOk()) // 성공 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }
    }

    @Test
    void deleteAccount() {
    }

    @Test
    void restoreAccount() {
    }

    @Test
    void accountList() {
    }

    @Test
    void detailList() {
    }
}