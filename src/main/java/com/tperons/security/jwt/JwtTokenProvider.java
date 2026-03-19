package com.tperons.security.jwt;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tperons.dto.security.TokenDTO;
import com.tperons.exception.InvalidJwtAuthenticationException;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtTokenProvider {

    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String TYPE_ACCESS      = "access";
    private static final String TYPE_REFRESH     = "refresh";

    private final UserDetailsService userDetailsService;

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-lenght}")
    private long validityInMillis;

    private Algorithm algorithm;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    public TokenDTO createAccessToken(String username, List<String> roles) {
        Instant now = Instant.now();
        Instant validity = now.plusMillis(validityInMillis);
        String accessToken = getAccessToken(username, roles, now, validity);
        String refreshToken = getRefreshToken(username, roles, now);
        return new TokenDTO(username, true, now, validity, accessToken, refreshToken);
    }

    public TokenDTO refreshToken(String refreshToken) {
        var token = "";
        if (StringUtils.isNotBlank(refreshToken) && refreshToken.startsWith("Bearer ")) {
            token = refreshToken.substring("Bearer ".length());
        }

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        String tokenType = decodedJWT.getClaim(CLAIM_TOKEN_TYPE).asString();
        if (!TYPE_REFRESH.equals(tokenType)) {
            throw new InvalidJwtAuthenticationException("Invalid token type for refresh");
        }

        String username = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        return createAccessToken(username, roles);
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            DecodedJWT decodedJWT = decodedToken(token);
            return !decodedJWT.getExpiresAt().before(Date.from(Instant.now()));
        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or Invalid JWT Token");
        }
    }

    private String getAccessToken(String username, List<String> roles, Instant now, Instant validity) {
        String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return JWT
                .create()
                .withClaim("roles", roles)
                .withClaim(CLAIM_TOKEN_TYPE, TYPE_ACCESS)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(validity))
                .withSubject(username)
                .withIssuer(issuerUrl)
                .sign(algorithm);
    }

    private String getRefreshToken(String username, List<String> roles, Instant now) {
        Instant refreshTokenValidity = now.plusMillis(validityInMillis * 6);
        return JWT
                .create()
                .withClaim("roles", roles)
                .withClaim(CLAIM_TOKEN_TYPE, TYPE_REFRESH)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(refreshTokenValidity))
                .withSubject(username)
                .sign(algorithm);
    }

    private DecodedJWT decodedToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
        // Algorithm alg = Algorithm.HMAC256(secretKey.getBytes());
        // JWTVerifier verifier = JWT.require(alg).build();
        // DecodedJWT decodedJWT = verifier.verify(token);
        // return decodedJWT;
    }

}
