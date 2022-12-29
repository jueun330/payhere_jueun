package com.example.payhere.shared.jwt;

import com.example.payhere.member.domain.Member;
import com.example.payhere.shared.domain.*;
import com.example.payhere.shared.repository.RefreshTokenRepository;
import com.example.payhere.shared.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 3;       //3시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;     //7일

    private final Key key;

    private final RefreshTokenRepository refreshTokenRepository;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(Member member) {
        long now = (new Date().getTime());

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(member.getEmail())
                .claim(AUTHORITIES_KEY, Authority.MEMBER.toString())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshTokenObject = RefreshToken.builder()
                .id(member.getMemberId())
                .member(member)
                .value(refreshToken)
                .build();

        refreshTokenRepository.save(refreshTokenObject);

        return TokenDto.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();

    }

    public Member getMemberFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return null;
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getMember();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
        } catch (ExpiredJwtException e) {
        } catch (UnsupportedJwtException e) {
        } catch (IllegalArgumentException e) {
        }
        return false;
    }

    @Transactional(readOnly = true)
    public RefreshToken isPresentRefreshToken(Member member) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMember(member);
        return optionalRefreshToken.orElse(null);
    }

    @Transactional
    public ResponseEntity<PrivateResponseBody> deleteRefreshToken(Member member) {
        RefreshToken refreshToken = isPresentRefreshToken(member);
        if (null == refreshToken) {
            return new ResponseEntity<>(new PrivateResponseBody
                    (StatusCode.LOGIN_WRONG_SIGNATURE_JWT_TOKEN, null), HttpStatus.OK);
        }

        refreshTokenRepository.delete(refreshToken);
        return new ResponseEntity<>(new PrivateResponseBody
                (StatusCode.OK, "로그아웃"), HttpStatus.OK);
    }
}