package com.plaza.usuarios_service.domain.usecase;

import com.plaza.usuarios_service.domain.exception.UsuarioException;
import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.JwtTokenPort;
import com.plaza.usuarios_service.domain.spi.PasswordEncoderPort;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;

public class LoginUseCase {

    private final UsuarioPersistencePort usuarioPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtTokenPort jwtTokenPort;

    public LoginUseCase(
            UsuarioPersistencePort usuarioPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            JwtTokenPort jwtTokenPort) {

        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtTokenPort = jwtTokenPort;
    }

    public Usuario login(String correo, String clave) {

        if (correo == null || correo.isBlank()
                || clave == null || clave.isBlank()) {
            throw new UsuarioException("Correo y clave son obligatorios");
        }

        Usuario usuario = usuarioPersistencePort.obtenerPorCorreo(correo);

        if (usuario == null) {
            throw new UsuarioException("Usuario no encontrado");
        }

        if (!passwordEncoderPort.matches(clave, usuario.getClave())) {
            throw new UsuarioException("Clave incorrecta");
        }


        return usuario;
    }

}