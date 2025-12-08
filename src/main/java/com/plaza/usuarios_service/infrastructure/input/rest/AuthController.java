package com.plaza.usuarios_service.infrastructure.input.rest;

import com.plaza.usuarios_service.application.dto.request.LoginRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.LoginHandler;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginHandler loginHandler;

    public AuthController(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    @PostMapping("/login")
    public UsuarioResponse login(@RequestBody LoginRequest request) {
        return loginHandler.login(request);
    }
}
