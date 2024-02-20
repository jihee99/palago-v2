package com.ex.palago.security.jwt;

import com.ex.palago.common.util.SeoulDateTimeHolder;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Log4j2
@Service
@RequiredArgsConstructor
public class JwtTokenService {
// => JwtTokenProvider
    private final SecretHolder secretHolder;
    private final SeoulDateTimeHolder seoulDateTimeHolder;

    private static final Duration ACCESS_TOKEN_EXPIRATION_TIME = Duration.ofMinutes(30);

    public Token createToken(String username, String roleKey) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", roleKey);

        long seoulTimeNow = seoulDateTimeHolder.getSeoulMilliseconds();
        final Key encodedKey = getSecretKey();
        String jwt = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(seoulTimeNow))
            .setExpiration(new Date(seoulTimeNow + ACCESS_TOKEN_EXPIRATION_TIME.toMillis()))
            .signWith(encodedKey)
            .compact();

        return new Token(jwt);
    }

    public boolean validateTokenExpirationTimeNotExpired(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
//            Jwts.parser()
//                .setSigningKey(secretHolder.getSecret())
//                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException exception) {
            return false;
        } catch (JwtException | IllegalArgumentException exception) {
            log.info("[토큰 에러] {}", exception::getMessage);
            return false;
        }
    }

    public String getUsername(String token) {
//        return Jwts.parser()
//            .setSigningKey(secretHolder.getSecret())
//            .parseClaimsJws(token)
//            .getBody()
//            .getSubject();
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey()).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(secretHolder.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
