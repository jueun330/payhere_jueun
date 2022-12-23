package com.example.payhere.account.service;

import com.example.payhere.account.controller.dto.AccountRequestDto;
import com.example.payhere.account.controller.dto.AccountResponseDto;
import com.example.payhere.account.domain.Account;
import com.example.payhere.account.domain.QAccount;
import com.example.payhere.account.repository.AccountRepository;
import com.example.payhere.member.domain.Member;
import com.example.payhere.member.repository.MemberRepository;
import com.example.payhere.shared.domain.PrivateResponseBody;
import com.example.payhere.shared.domain.StatusCode;
import com.example.payhere.shared.exception.PrivateException;
import com.example.payhere.shared.jwt.TokenProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.example.payhere.account.domain.QAccount.account;
import static com.example.payhere.member.domain.QMember.member;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final JPAQueryFactory jpaQueryFactory;


    // 가계부 작성
    @Transactional
    public ResponseEntity<PrivateResponseBody> createAccount(AccountRequestDto requestDto, HttpServletRequest request){

        // 회원 확인
        Member member = authorizeToken(request);

        // 저장 할 가계부 내용
        Account account = Account.builder()
                .member(member)
                .money(requestDto.getMoney())
                .memo(requestDto.getMemo())
                .build();

        // repo에 저장
        accountRepository.save(account);

        // 반환 값
        AccountResponseDto responseDto = AccountResponseDto.builder()
                .money(requestDto.getMoney())
                .memo(requestDto.getMemo())
                .build();

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, responseDto), HttpStatus.OK);
    }

    // 가계부 수정
    @Transactional
    public ResponseEntity<PrivateResponseBody> updateAccount(AccountRequestDto requestDto, Long accountId, HttpServletRequest request){

        // 회원 확인
        Member member = authorizeToken(request);

        // 수정할 가계부 찾기
        Account account = accountRepository.findById(accountId).get();

        // 수정할 가계부가 존재하지 않을 때 에러 메시지 반환
        if (null == account) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }

        // 내용 수정
        account.update(requestDto);

        // 반환 값
        AccountResponseDto responseDto = AccountResponseDto.builder()
                .id(accountId)
                .money(requestDto.getMoney())
                .memo(requestDto.getMemo())
                .build();

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, responseDto), HttpStatus.OK);
    }


    // 가계부 삭제
    @Transactional
    public ResponseEntity<PrivateResponseBody> deleteAccount(HttpServletRequest request, Long accountId){

        // 회원 확인
        Member member = authorizeToken(request);

        // 삭제할 가계부 찾기
        Account account = accountRepository.findById(accountId).get();

        // 삭제할 가계부가 존재하지 않을 때 에러 메시지 반환
        if (null == account) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }

        // 삭제 처리
        account.delete();

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, "가계부 삭제 완료!"), HttpStatus.OK);
    }


    // 작성된 가계부 목록
    public ResponseEntity<PrivateResponseBody> accountList(HttpServletRequest request){

        // 회원 확인
        Member member = authorizeToken(request);

        // repo에서 회원이 작성하였고 삭제하지 않은 가계부 리스트 찾기
        List<Account> accountList = jpaQueryFactory
                .selectFrom(account)
                .where(account.member.memberId.eq(member.getMemberId())
                , account.deleted.eq(Boolean.FALSE))
                .orderBy(account.createdAt.desc()) // 최신 순으로 정렬
                .fetch();


        // 순서대로 response에 담기
        List<AccountResponseDto> responseDtoList = new LinkedList<>();
        for(Account account : accountList){
            responseDtoList.add(AccountResponseDto.builder() // 전체 리스트에서는 금액과 아이디만 보여주기
                            .id(account.getAccountId())
                            .money(account.getMoney())
                            .build());
        }

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, responseDtoList), HttpStatus.OK);
    }

    // 가계부 상세 조회
    public ResponseEntity<PrivateResponseBody> accountDetail(HttpServletRequest request){

        // 회원 확인
        Member member = authorizeToken(request);

        // repo에서 회원이 작성하였고 삭제하지 않은 가계부 리스트 찾기
        List<Account> accountList = jpaQueryFactory
                .selectFrom(account)
                .where(account.member.memberId.eq(member.getMemberId())
                        , account.deleted.eq(Boolean.FALSE))
                .orderBy(account.createdAt.desc()) // 최신 순으로 정렬
                .fetch();


        // 순서대로 response에 담기
        List<AccountResponseDto> responseDtoList = new LinkedList<>();
        for(Account account : accountList){
            responseDtoList.add(AccountResponseDto.builder() // 상세 조회에서는 모든 내용 보여주기
                            .id(account.getAccountId())
                            .money(account.getMoney())
                            .memo(account.getMemo())
                            .createdAt(account.getCreatedAt())
                            .modifiedAt(account.getModifiedAt())
                            .build());
        }

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, responseDtoList), HttpStatus.OK);
    }

    // 삭제한 가계부 복구하기
    @Transactional
    public ResponseEntity<PrivateResponseBody> restoreDeleted(HttpServletRequest request, Long accountId){

        // 회원 확인
        Member member = authorizeToken(request);

        // 복구할 가계부 찾기
        Account account = accountRepository.findById(accountId).get();

        // 복구할 가계부가 존재하지 않을 때 에러 메시지 반환
        if (null == account) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.OK);
        }

        // 복구 처리
        account.restore();

        AccountResponseDto responseDto = AccountResponseDto.builder()
                .id(accountId)
                .money(account.getMoney())
                .memo(account.getMemo())
                .build();

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, responseDto), HttpStatus.OK);
    }

    // 토큰 확인
    public Member authorizeToken(HttpServletRequest request) {

        // Access 토큰 유효성 확인
        if (request.getHeader("Authorization") == null) {
            throw new PrivateException(StatusCode.LOGIN_WRONG_SIGNATURE_JWT_TOKEN);
        }

        // Refresh 토큰 유요성 확인
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            throw new PrivateException(StatusCode.LOGIN_WRONG_SIGNATURE_JWT_TOKEN);
        }

        // Access, Refresh 토큰 유효성 검증이 완료되었을 경우 인증된 유저 정보 저장
        Member member = tokenProvider.getMemberFromAuthentication();

        // 인증된 유저 정보 반환
        return member;
    }
}
