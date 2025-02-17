package com.simol.ounuser.user.service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.simol.ouncommon.auth.entity.UserEntity;
import com.simol.ouncommon.auth.repository.UsersRepository;
import com.simol.ouncommon.auth.vo.AuthTokenResponse;
import com.simol.ouncommon.auth.vo.Token;
import com.simol.ounuser.config.jwt.JwtProvider;
import com.simol.ounuser.user.vo.GoogleUserInfoResponse;
import com.simol.ounuser.user.vo.RedirectUrlResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {
    private final JwtProvider jwtProvider;
    private final RestClient restClient;
    private final UsersRepository usersRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${oauth2.google.client-id}")
    private String clientId;

    @Value("${oauth2.google.client-secret}")
    private String clientSecret;

    @Transactional
    public AuthTokenResponse authenticateWithGoogle(String googleToken) {
        log.debug("accessToken: {}", googleToken);
        final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=%s".formatted(googleToken);
        // 구글에서 계정 정보 받아오기
        GoogleUserInfoResponse googleUserInfoResponse = restClient.get().uri(GOOGLE_USER_INFO_URL)
            .retrieve()
            .body(GoogleUserInfoResponse.class);
        // 유저 정보 확인 후 저장하기
        UserEntity user = usersRepository.findByEmail(googleUserInfoResponse.getEmail())
            .orElseGet(() -> 
                UserEntity.create(
                    googleUserInfoResponse.getEmail(), 
                    googleUserInfoResponse.getName(), 
                    googleUserInfoResponse.getPicture(), 
                    "ROLE_USER"
                )
            );
        usersRepository.save(user);
        // access token 발급, refresh token 발급
        LocalDateTime now = LocalDateTime.now();

        final long REFRESH_TOKEN_EXPIRATION_TIME = 360L;
        final long ACCESS_TOKEN_EXPIRATION_TIME = 60L;

        Token refreshToken = jwtProvider.createRefreshToken(user, now, REFRESH_TOKEN_EXPIRATION_TIME);
        Token accessToken = jwtProvider.createAccessToken(user, now, ACCESS_TOKEN_EXPIRATION_TIME);
        
        AuthTokenResponse authTokenResponse = AuthTokenResponse.of(accessToken.getToken(), refreshToken.getToken(), accessToken.getExpiredAt(), refreshToken.getExpiredAt());

        // redis 적용
        String refreshTokenAsString = refreshToken.getToken();
        // 토큰 만료 시간 설정
        redisTemplate.opsForValue().set(refreshTokenAsString, user.getEmail(), REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MINUTES);
        // 발급된 토큰 반환
        return authTokenResponse;
    }

    public RedirectUrlResponse redirectUrlByGoogle(String redirectUri) {
        return RedirectUrlResponse.googleOf(clientId, redirectUri);
    }
    
}
