package com.plaza.usuarios_service.application.handler;

import com.plaza.usuarios_service.application.dto.response.LoginResponse;
import com.plaza.usuarios_service.application.dto.request.LoginRequest;

public interface LoginHandler {


    LoginResponse login(LoginRequest request);
}