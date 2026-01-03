package com.plaza.usuarios_service.application.handler.Impl;

import com.plaza.usuarios_service.application.dto.request.LoginRequest;
import com.plaza.usuarios_service.application.dto.response.LoginResponse;
import com.plaza.usuarios_service.application.handler.LoginHandler;
import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.JwtTokenPort;
import com.plaza.usuarios_service.domain.usecase.LoginUseCase;
import org.springframework.stereotype.Service;

@Service
public class LoginHandlerImpl implements LoginHandler {

    private final LoginUseCase loginUseCase;
    private final JwtTokenPort jwtTokenPort;

    public LoginHandlerImpl(
            LoginUseCase loginUseCase,
            JwtTokenPort jwtTokenPort) {

        this.loginUseCase = loginUseCase;
        this.jwtTokenPort = jwtTokenPort;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        Usuario usuario = loginUseCase.login(
                request.getCorreo(),
                request.getClave()
        );

        String token = jwtTokenPort.generarToken(usuario.getCorreo(), usuario.getRol());


        return new LoginResponse(
                usuario.getNombre(),
                usuario.getRol(),
                token
        );
    }
}
