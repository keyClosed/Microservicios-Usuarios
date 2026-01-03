package com.plaza.usuarios_service.infrastructure.input.rest;

import com.plaza.usuarios_service.application.dto.request.LoginRequest;
import com.plaza.usuarios_service.application.dto.response.LoginResponse;
import com.plaza.usuarios_service.application.handler.LoginHandler;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final LoginHandler loginHandler;

    public LoginController(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return loginHandler.login(request);
    }
}