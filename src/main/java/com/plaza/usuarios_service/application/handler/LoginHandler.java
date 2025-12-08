package com.plaza.usuarios_service.application.handler;

import com.plaza.usuarios_service.application.dto.request.LoginRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.mapper.UsuarioMapper;
import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.usecase.LoginUseCase;
import org.springframework.stereotype.Service;

@Service
public class LoginHandler {

    private final LoginUseCase loginUseCase;

    public LoginHandler(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    public UsuarioResponse login(LoginRequest request) {
        Usuario usuario = loginUseCase.login(request.getCorreo(), request.getClave());
        return UsuarioMapper.toResponse(usuario);
    }
}
