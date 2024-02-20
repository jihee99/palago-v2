package com.ex.palago.security.jwt;

import com.ex.palago.common.util.SeoulDateTimeHolder;
import io.jsonwebtoken.*;

import java.time.Duration;
import java.util.Date;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final SecretHolder secretHolder;
    private final SeoulDateTimeHolder seoulDateTimeHolder;

    private static final Duration ACCESS_TOKEN_EXPIRATION_TIME = Duration.ofMinutes(30);
    public JwtTokenInfo createToken(String univId, String roleKey) {
        Claims claims = Jwts.claims().setSubject(univId);
        claims.put("role", roleKey);

        long seoulTimeNow = seoulDateTimeHolder.getSeoulMilliseconds();

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(seoulTimeNow))
                .setExpiration(new Date(seoulTimeNow + ACCESS_TOKEN_EXPIRATION_TIME.toMillis()))
                .signWith(SignatureAlgorithm.HS256, secretHolder.getSecret())
                .compact();

        return new JwtTokenInfo(jwt);
    }

    public boolean validateTokenExpirationTimeNotExpired(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            Jwts.parser()
                    .setSigningKey(secretHolder.getSecret())
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException exception) {
            return false;
        } catch (JwtException | IllegalArgumentException exception) {
            log.info("[토큰 에러] ");
//            log.info("[토큰 에러] ", exception::getMessage);
            return false;
        }
    }

}
