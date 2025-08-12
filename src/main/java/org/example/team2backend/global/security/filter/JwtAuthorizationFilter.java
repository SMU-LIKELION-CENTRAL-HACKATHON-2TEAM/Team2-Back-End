package org.example.team2backend.global.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.global.security.auth.CustomUserDetails;
import org.example.team2backend.global.security.auth.CustomUserDetailsService;
import org.example.team2backend.global.security.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (
                uri.equals("/favicon.ico") ||
                        uri.startsWith("/swagger") ||
                        uri.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("[ JwtAuthorizationFilter ] 인가 필터 작동");

        try {
            // 1. Request에서 Access Token 추출
            String accessToken = jwtUtil.resolveAccessToken(request);

            // 2. Access Token이 없으면 다음 필터로 바로 진행
            if (accessToken == null) {
                log.info("[ JwtAuthorizationFilter ] Access Token 이 존재하지 않음. 필터를 건너뜁니다.");
                filterChain.doFilter(request, response);
                return;
            }

            // 3. Access Token을 이용한 인증 처리
            //토큰 유효성 검증 -> CustomUserDetails 생성 -> 인증 객체 생성
            authenticateAccessToken(accessToken);
            log.info("[ JwtAuthorizationFilter ] 종료. 다음 필터로 넘어갑니다.");
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            // 4. 토큰 만료 시 401 응답 처리
            log.warn("[ JwtAuthorizationFilter ] accessToken 이 만료되었습니다.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Access Token 이 만료되었습니다.");
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    private void authenticateAccessToken(String accessToken) throws SignatureException {
        log.info("[ JwtAuthorizationFilter ] 토큰으로 인가 과정을 시작합니다. ");

        // 1. Access Token의 유효성 검증
        jwtUtil.validateToken(accessToken);
        log.info("[ JwtAuthorizationFilter ] Access Token 유효성 검증 성공. ");

        // 2. Access Token에서 사용자 정보 추출 후 CustomUserDetails 생성
        //2-1. 이메일 추출
        String email = jwtUtil.getEmail(accessToken);

        CustomUserDetails userDetails = (CustomUserDetails)customUserDetailsService.loadUserByUsername(email);

        log.info("[ JwtAuthorizationFilter ] UserDetails 객체 생성 성공");

        // 3. 인증 객체 생성 및 SecurityContextHolder에 저장
        //이 인증 객체를 바탕으로 이후 컨트롤러에 접근할 수 있도록 인가
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,  //원래는 password가 들어가야 하지만, 여기선 null
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.info("[ JwtAuthorizationFilter ] 인증 객체 저장 완료");
    }

}
