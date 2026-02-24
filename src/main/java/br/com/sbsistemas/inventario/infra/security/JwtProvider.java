package br.com.sbsistemas.inventario.infra.security;

import br.com.sbsistemas.inventario.infra.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final AppProperties appProperties;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(appProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generate(String email, Long tenantId, String perfil, Long userId, String userName) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(email)
                .claim("tenantId", tenantId)
                .claim("perfil", perfil)
                .claim("userId", userId)
                .claim("userName", userName)
                .issuedAt(new Date(now))
                .expiration(new Date(now + appProperties.getJwt().getExpirationMs()))
                .signWith(key())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Token JWT inválido: {}", ex.getMessage());
            return false;
        }
    }
}
