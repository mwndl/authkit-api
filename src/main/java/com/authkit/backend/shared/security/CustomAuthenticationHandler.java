package com.authkit.backend.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.authkit.backend.shared.exception.ApiErrorCode;
import com.authkit.backend.shared.exception.ApiErrorResponse;
import com.authkit.backend.shared.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ApiException apiException = new ApiException(ApiErrorCode.UNAUTHENTICATED);

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                apiException.getErrorCode().name(),
                apiException.getErrorCode().getTitle(),
                apiException.getErrorCode().getDescription()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
