package org.example.team2backend.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.team2backend.domain.member.dto.request.MemberReqDTO;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.example.team2backend.global.apiPayload.code.AuthErrorCode;
import org.example.team2backend.global.security.auth.AuthException;
import org.example.team2backend.global.security.auth.CustomUserDetails;
import org.example.team2backend.global.security.jwt.JwtDTO;
import org.example.team2backend.global.security.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    //로그인 시도 메서드
    @Override
    public Authentication attemptAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response) throws AuthenticationException {

        log.info("[ Login Filter ]  로그인 시도 : Custom Login Filter 작동 ");
        //Object Mapper 생성
        ObjectMapper objectMapper = new ObjectMapper();
        //로그인 DTO 생성
        MemberReqDTO.SignUpRequestDTO requestBody;
        try {
            //만든 DTO에 request의 값을 파싱해서 주입
            requestBody = objectMapper.readValue(request.getInputStream(), MemberReqDTO.SignUpRequestDTO.class);
        } catch (IOException e) {
            throw new AuthException(AuthErrorCode.NOT_FOUND_404);
        }

        //Request Body 에서 이메일, 패스워드 추출
        String email = requestBody.email(); //Email 추출
        String password = requestBody.password(); //password 추출
        log.info("[ Login Filter ]  Email ---> {} ", email);
        log.info("[ Login Filter ]  Password ---> {} ", password);

        //인증용 객체 생성
        UsernamePasswordAuthenticationToken authToken
                = new UsernamePasswordAuthenticationToken(email, password, null);


        log.info("[ Login Filter ] 인증용 객체 UsernamePasswordAuthenticationToken 이 생성되었습니다. ");
        log.info("[ Login Filter ] 인증을 시도합니다.");

        //인증 시도
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시
    @Override
    protected void successfulAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain,
            @NonNull Authentication authentication) throws IOException {


        log.info("[ Login Filter ] 로그인에 성공 하였습니다.");

        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();


        //Client 에게 줄 Response 를 생성
        JwtDTO jwtDto = new JwtDTO(
                jwtUtil.createJwtAccessToken(customUserDetails),
                jwtUtil.createJwtRefreshToken(customUserDetails)
        );

        // CustomResponse 사용하여 응답 통일
        CustomResponse<JwtDTO> responseBody = CustomResponse.onSuccess(jwtDto);

        //JSON 변환
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value()); //Response 의 Status 를 200으로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        //Body 에 토큰이 담긴 Response 쓰기
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
    @Override
    protected void unsuccessfulAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException failed) throws IOException {

        log.info("[ Login Filter ] 로그인에 실패하였습니다.");

        String errorCode;
        String errorMessage;

        if (failed instanceof BadCredentialsException) {
            errorCode = String.valueOf(HttpStatus.UNAUTHORIZED.value());
            errorMessage = "잘못된 정보입니다.";
        } else if (failed instanceof LockedException) {
            errorCode = String.valueOf(HttpStatus.LOCKED.value());
            errorMessage = "계정이 잠금 상태입니다.";
        } else if (failed instanceof DisabledException) {
            errorCode = String.valueOf(HttpStatus.FORBIDDEN.value());
            errorMessage = "계정이 비활성화 되었습니다.";
        } else if (failed instanceof UsernameNotFoundException) {
            errorCode = String.valueOf(HttpStatus.NOT_FOUND.value());
            errorMessage = "계정을 찾을 수 없습니다.";
        } else if (failed instanceof AuthenticationServiceException) {
            errorCode = String.valueOf(HttpStatus.BAD_REQUEST.value());
            errorMessage = "Request Body 파싱 중 오류가 발생했습니다.";
        } else {
            errorCode = String.valueOf(HttpStatus.UNAUTHORIZED.value());
            errorMessage = "인증에 실패했습니다.";
        }

        // CustomResponse 사용하여 응답 통일
        CustomResponse<JwtDTO> responseBody = CustomResponse.onFailure(errorCode, errorMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(Integer.parseInt(errorCode)); // HTTP 상태 코드 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody)); // 응답 변환 및 출력
    }
}
