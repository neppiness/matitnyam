package com.wanted.matitnyam.service;

import com.wanted.matitnyam.domain.Authority;
import com.wanted.matitnyam.dto.PrincipalDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthTokenServiceTest {

    private static final String name = "neppiness";

    private static final Double latitude = 37.146192955;

    private static final Double longitude = 127.0693630667;

    @Autowired
    private AuthTokenService authTokenService;

    @DisplayName("토큰 생성 테스트")
    @Test
    void createTokenTest() {
        PrincipalDto principal = PrincipalDto.builder()
                .name(name)
                .authority(Authority.USER)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        System.out.println(authTokenService.createToken(principal));
    }

    @DisplayName("유효한 토큰 파싱 테스트")
    @Test
    void parseValidTokenTest() {
        PrincipalDto givenPrincipal = PrincipalDto.builder()
                .name(name)
                .authority(Authority.USER)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        String token = authTokenService.createToken(givenPrincipal);
        System.out.println("token = " + token);

        PrincipalDto parsedPrincipal = authTokenService.parseToken(token);
        Assertions
                .assertThat(parsedPrincipal.name())
                .isEqualTo(name);
        Assertions
                .assertThat(parsedPrincipal.authority())
                .isEqualTo(Authority.USER);
    }

    @DisplayName("유효하지 않은 토큰 파싱 테스트")
    @Test
    void parseInvalidTokenTest() {
        String invalidToken = "eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9"
                + ".eyJzdWIiOiJuZXBwaW5lc3MiLCJpYXQiOjE3MDA5ODQ4NjEsImV4cCI6MTcwMDk4NjY2MSwiYXV0aCI6IlVTRVIifQ"
                + ".u5wOyEeDh-68d10XeAmGwnbsLbVjSUu1ICpxe0o6tLP";

        Assertions
                .assertThatThrownBy(() -> authTokenService.parseToken(invalidToken))
                .isInstanceOf(SignatureException.class);
    }

    @DisplayName("만료된 토큰 테스트")
    @Test
    void parseExpiredTokenTest() {
        /*
        HEADER: { "typ": "jwt", "alg": "HS256" }
        PAYLOAD: { "sub": "neppiness", "iat": 1500984861, "exp": 1500986661, "auth": "USER" }
         */
        String expiredToken = "eyJ0eXAiOiJqd3QiLCJhbGciOiJIUzI1NiJ9"
                + ".eyJzdWIiOiJuZXBwaW5lc3MiLCJpYXQiOjE1MDA5ODQ4NjEsImV4cCI6MTUwMDk4NjY2MSwiYXV0aCI6IlVTRVIifQ"
                + ".ozmVVI40aidztXYkjefxAomvUXIs1eJesqXu_RYUTIU";

        Assertions
                .assertThatThrownBy(() -> authTokenService.parseToken(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

}