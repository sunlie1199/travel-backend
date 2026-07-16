package com.travel.module.auth.service;

import com.travel.module.auth.dto.LoginRequest;
import com.travel.module.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
