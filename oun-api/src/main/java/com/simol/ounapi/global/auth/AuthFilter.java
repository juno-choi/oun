package com.simol.ounapi.global.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter{

    private final ApiJwtTokenProvider apiJwtTokenProvider;

    private final List<String> WHITE_LIST = Arrays.asList(
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/h2-console",
        "/h2-console/**",
        "/test/**"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        return WHITE_LIST.stream()
        .anyMatch(pattern -> 
            new AntPathMatcher().match(pattern, requestURI));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("requestURI: {}", request.getRequestURI());

        // 인증 처리
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        Authentication authentication = apiJwtTokenProvider.getAuthentication(token);

        request.setAttribute("memberId", authentication.getName());
        request.setAttribute("authorities", authentication.getAuthorities());
        
        // 정상 처리
        filterChain.doFilter(request, response);
    }
}
