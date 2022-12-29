package com.example.payhere.account.controller;

import com.example.payhere.account.controller.dto.AccountRequestDto;
import com.example.payhere.account.repository.AccountRepository;
import com.example.payhere.member.domain.Member;
import com.example.payhere.shared.jwt.TokenDto;
import com.example.payhere.shared.jwt.TokenProvider;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails(value = "teset00@test.com")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Nested
@DisplayName("Account Controller 테스트")
class AccountControllerTest {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    MockMvc mvc;

    @Autowired
    TokenProvider tokenProvider;

    String accessToken;
    String refreshToken;
    Long accessTokenExpiresIn;

    @BeforeAll
    public void setup() {
        TokenDto tokenDto = tokenProvider.generateTokenDto(new Member(7L, "teset00@test.com", "asdf1234", null));
        accessToken = tokenDto.getAccessToken();
        refreshToken = tokenDto.getRefreshToken();
        accessTokenExpiresIn = tokenDto.getAccessTokenExpiresIn();
    }

    @Nested
    @DisplayName("가계부 작성 테스트(금액, 메모)")
    class CreateTest {
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
                            .with(csrf())// 403 에러를 방지하기 위한 csrf
                            .header("Authorization", "Bearer " + accessToken)
                            .header("Refresh-Token", refreshToken)
                            .header("Access-Token-Expire-Time", accessTokenExpiresIn)
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
                    .andExpect(status().is4xxClientError()) // 에러 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }
    }

    @Nested
    @DisplayName("가계부 수정 테스트(금액, 메모)")
    class UpdateTest {
        @DisplayName("수정 성공")
        @Test
        void updateAccount() throws Exception {

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
    @Nested
    @DisplayName("가계부 삭제 테스트")
    class DeleteTest {
        @DisplayName("삭제 성공")
        @Test
        void deleteAccount() throws Exception {

            // api 전송
            mvc.perform(delete("/ph/accounts/1")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON))// json 형식으로 데이터를 보낸다고 명시
                    .andExpect(status().isOk()) // 성공 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }
        @DisplayName("삭제 실패")
        @Test
        void deleteAccount_fail() throws Exception {

            // api 전송
            mvc.perform(delete("/ph/accounts/999")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON))// json 형식으로 데이터를 보낸다고 명시
                    .andExpect(status().is4xxClientError()) // 에러 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }
    }
    @Nested
    @DisplayName("가계부 복구 테스트")
    class RestoreTest {
        @DisplayName("복구 성공")
        @Test
        void restoreAccount() throws Exception {

            // api 전송
            mvc.perform(get("/ph/accounts/restore/1")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON))// json 형식으로 데이터를 보낸다고 명시
                    .andExpect(status().isOk()) // 성공 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }
        @DisplayName("복구 실패")
        @Test
        void restoreAccount_fail() throws Exception {

            // api 전송
            mvc.perform(get("/ph/accounts/restore/999")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON))// json 형식으로 데이터를 보낸다고 명시
                    .andExpect(status().is4xxClientError()) // 에러 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }
    }

    @Nested
    @DisplayName("가계부 조회 테스트")
    class GetAccountTest {
        @DisplayName("목록 조회 성공")
        @Test
        void accountList() throws Exception {

            // api 전송
            mvc.perform(get("/ph/accounts")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON))// json 형식으로 데이터를 보낸다고 명시
                    .andExpect(status().is4xxClientError()) // 성공 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }
        @DisplayName("세부 조회 성공")
        @Test
        void detailList() throws Exception {

            // api 전송
            mvc.perform(get("/ph/accounts/details")// 요청 전송
                            .with(csrf()) // 403 에러를 방지하기 위한 csrf
                            .contentType(MediaType.APPLICATION_JSON))// json 형식으로 데이터를 보낸다고 명시
                    .andExpect(status().is4xxClientError()) // 성공 코드 반환
                    .andDo(print()); // 요청과 응답 정보 전체 출력
        }
    }
}