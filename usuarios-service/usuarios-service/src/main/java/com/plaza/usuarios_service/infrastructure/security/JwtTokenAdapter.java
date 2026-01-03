package com.plaza.usuarios_service.infrastructure.security;

import com.plaza.usuarios_service.domain.spi.JwtTokenPort;
import com.plaza.usuarios_service.infrastructure.security.JwtUtil;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenAdapter implements JwtTokenPort {

    private final JwtUtil jwtUtil;

    public JwtTokenAdapter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String generarToken(String correo, String rol) {
        return jwtUtil.generateToken(correo, rol);
    }

    @Override
    public boolean validarToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public String obtenerCorreo(String token) {
        return jwtUtil.getCorreoFromToken(token);
    }

    @Override
    public String obtenerRol(String token) {
        return jwtUtil.getRolFromToken(token);
    }
}