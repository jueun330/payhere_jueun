package com.example.payhere.account.domain;

import com.example.payhere.account.controller.dto.AccountRequestDto;
import com.example.payhere.member.domain.Member;
import com.example.payhere.shared.domain.Timestamped;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account extends Timestamped {

    // 고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    // 메모
    @Column
    private String memo;

    // 지출 내역
    @Column
    private Integer money;

    // 삭제 여부
    @Column
    private Boolean deleted = Boolean.FALSE;

    // 회원 정보
    @JoinColumn(name = "memberId", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    // 수정
    public void update(AccountRequestDto requestDto){
        this.money = requestDto.getMoney();
        this.memo = requestDto.getMemo();
    }

    // 삭제
    public void delete(){
        this.deleted = Boolean.TRUE;
    }

    // 복구
    public void restore(){
        this.deleted = Boolean.FALSE;
    }

    // 회원 확인
    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }
}
