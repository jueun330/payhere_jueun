package com.example.payhere.member.domain;

import com.example.payhere.account.domain.Account;
import com.example.payhere.shared.domain.Timestamped;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.awt.print.Book;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends Timestamped {
    //고유 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    //이메일
    @Column(nullable = false)
    private String email;

    //비밀번호
    @Column(nullable = false)
    @JsonIgnore
    private String pw;

    // 가계부와 연관관계
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Account> accounts = new LinkedHashSet<>();

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // 비밀번호 검증
    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.pw);
    }
}