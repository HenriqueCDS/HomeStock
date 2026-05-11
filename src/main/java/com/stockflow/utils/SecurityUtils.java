package com.stockflow.utils;

import com.stockflow.exception.BusinessException;
import com.stockflow.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {

    private SecurityUtils() {}

    public static String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    public static UUID getCurrentUserId(JwtTokenProvider jwtTokenProvider, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) throw new BusinessException("Not authenticated", HttpStatus.UNAUTHORIZED);
        return jwtTokenProvider.getUserId(token);
    }

    public static UUID getCurrentTenantId(JwtTokenProvider jwtTokenProvider, HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) throw new BusinessException("Not authenticated", HttpStatus.UNAUTHORIZED);
        return jwtTokenProvider.getTenantId(token);
    }

    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated();
    }
}
