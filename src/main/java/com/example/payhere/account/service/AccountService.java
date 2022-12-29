package com.example.payhere.account.service;

import com.example.payhere.account.controller.dto.AccountRequestDto;
import com.example.payhere.account.controller.dto.AccountResponseDto;
import com.example.payhere.account.domain.Account;
import com.example.payhere.account.repository.AccountRepository;
import com.example.payhere.member.domain.Member;
import com.example.payhere.member.repository.MemberRepository;
import com.example.payhere.shared.domain.PrivateResponseBody;
import com.example.payhere.shared.domain.StatusCode;
import com.example.payhere.shared.exception.PrivateException;
import com.example.payhere.shared.jwt.TokenProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.example.payhere.account.domain.QAccount.account;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AccountRepository accountRepository;
    private final TokenProvider tokenProvider;
    private final JPAQueryFactory jpaQueryFactory;
    private final MemberRepository memberRepository;

    String now = LocalDateTime.now().toString();


    // 가계부 작성
    @Transactional
    public ResponseEntity<PrivateResponseBody> createAccount(AccountRequestDto requestDto, Long memberId){

        logger.info("url : ph/accounts");
        logger.info("method : post");
        logger.info("request_data : ", requestDto);
        logger.info("request_query_param : ", memberId);

        // 회원 확인
        Member member = memberRepository.findById(memberId).orElseThrow(
                ()-> new RuntimeException("존재하지 않는 사용자입니다.")
        );

        // 금액 누락 시 에러메시지 반환
        if(requestDto.getMoney() == null){
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.INVALID_PARAMETER, null), HttpStatus.BAD_REQUEST);
        }

        // 저장 할 가계부 내용
        Account account = Account.builder()
                .member(member)
                .money(requestDto.getMoney())
                .memo(requestDto.getMemo())
                .deleted(Boolean.FALSE)
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

        logger.info("url : ph/accounts");
        logger.info("method : put");
        logger.info("request_data : ", requestDto);
        logger.info("request_query_param : ", accountId);

        // 회원 확인
        Member member = authorizeToken(request);

        // 수정할 가계부 찾기
        Account account = isPresentAccount(accountId);

        // 수정할 가계부가 존재하지 않을 때 에러 메시지 반환
        if (null == account) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.BAD_REQUEST);
        }

        // 작성자가 아닐 때 에러 메시지 반환
        if (account.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST, null), HttpStatus.OK);
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
        Account account = isPresentAccount(accountId);

        // 삭제할 가계부가 존재하지 않을 때 에러 메시지 반환
        if (null == account) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.BAD_REQUEST);
        }

        // 작성자가 아닐 때 에러 메시지 반환
        if (account.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST, null), HttpStatus.BAD_REQUEST);
        }

        // 삭제 처리
        account.delete();

        return new ResponseEntity<>(new PrivateResponseBody<>(StatusCode.OK, "가계부 삭제 완료!"), HttpStatus.OK);
    }

    // 삭제한 가계부 복구하기
    @Transactional
    public ResponseEntity<PrivateResponseBody> restoreDeleted(HttpServletRequest request, Long accountId){

        // 회원 확인
        Member member = authorizeToken(request);

        // 복구할 가계부 찾기
        Account account = isPresentAccount(accountId);

        // 복구할 가계부가 존재하지 않을 때 에러 메시지 반환
        if (null == account) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.NOT_FOUND, null), HttpStatus.BAD_REQUEST);
        }

        // 작성자가 아닐 때 에러 메시지 반환
        if (account.validateMember(member)) {
            return new ResponseEntity<>(new PrivateResponseBody(StatusCode.BAD_REQUEST, null), HttpStatus.BAD_REQUEST);
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


    // 작성자 확인
    @Transactional
    public Member authorizeToken(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    // 존재하는 가계부인지 여부 확인
    @Transactional(readOnly = true)
    public Account isPresentAccount(Long accountId) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        return optionalAccount.orElse(null);
    }
}
