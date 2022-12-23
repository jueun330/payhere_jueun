package com.example.payhere.account.domain;

import com.example.payhere.account.controller.dto.AccountRequestDto;
import com.example.payhere.member.domain.Member;
import com.example.payhere.shared.domain.Timestamped;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account extends Timestamped {

    // 고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long accountId;

    // 메모
    @Column
    private String memo;

    // 지출 내역
    @Column
    private Integer money;

    // 삭제 여부
    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;

    // 작성 시간
    @Column
    private LocalDateTime createdAt;

    // 수정 시간
    @Column
    private LocalDateTime modifiedAt;

    // 회원 정보
    @JoinColumn(name = "memberId", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    public void update(AccountRequestDto requestDto){
        this.money = requestDto.getMoney();
        this.memo = requestDto.getMemo();
    }

    public void delete(){
        this.deleted = Boolean.TRUE;
    }

    public void restore(){
        this.deleted = Boolean.FALSE;
    }
}