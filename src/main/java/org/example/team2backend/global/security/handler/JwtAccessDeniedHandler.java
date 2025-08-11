package org.example.team2backend.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.team2backend.global.apiPayload.CustomResponse;
import org.example.team2backend.global.apiPayload.code.AuthErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        //content_type json으로 설정
        response.setContentType("application/json; charset=UTF-8");
        //상태 코드 403으로 설정
        response.setStatus(403);
        //실패 응답 객체 생성
        CustomResponse<Object> errorResponse = CustomResponse.onFailure(
                AuthErrorCode.FORBIDDEN_403.getCode(),
                AuthErrorCode.FORBIDDEN_403.getMessage(),
                null
        );
        //ObjectMapper를 사용하여 Json으로 변환 후 응답으로 출력
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }

}
