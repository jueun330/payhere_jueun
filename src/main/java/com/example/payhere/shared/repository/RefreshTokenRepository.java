package com.example.payhere.shared.repository;

import com.example.payhere.member.domain.Member;
import com.example.payhere.shared.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMember(Member member);
}
