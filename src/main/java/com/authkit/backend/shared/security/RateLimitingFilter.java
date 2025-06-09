package com.authkit.backend.shared.security;

import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.exception.ApiErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    private static class RequestInfo {
        int count;
        long timestamp;

        RequestInfo() {
            this.count = 1;
            this.timestamp = System.currentTimeMillis();
        }

        void increment() {
            this.count++;
        }

        void reset() {
            this.count = 1;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private final JwtUtil jwtUtil;
    private final Map<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        String userId;

        try {
            userId = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        RequestInfo info = requestCounts.compute(userId, (key, existing) -> {
            long now = System.currentTimeMillis();
            if (existing == null || now - existing.timestamp > 60_000) {
                return new RequestInfo();
            } else {
                existing.increment();
                return existing;
            }
        });

        if (info.count > MAX_REQUESTS_PER_MINUTE) {
            handlerExceptionResolver.resolveException(request, response, null, new ApiException(ApiErrorCode.TOO_MANY_REQUESTS));
            return;
        }


        filterChain.doFilter(request, response);
    }
}
