package com.travel.module.auth.service.impl;

import com.travel.common.audit.AuditLog;
import com.travel.common.exception.BusinessException;
import com.travel.common.result.ResultCode;
import com.travel.module.auth.dto.LoginRequest;
import com.travel.module.auth.dto.LoginResponse;
import com.travel.module.auth.security.JwtTokenProvider;
import com.travel.module.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @AuditLog(module = "AUTH", action = "LOGIN",
              resourceName = "#request.username", username = "#request.username")
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            String token = jwtTokenProvider.generateToken(authentication.getName());
            return LoginResponse.of(token, request.getUsername());
        } catch (Exception e) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }
    }
}
