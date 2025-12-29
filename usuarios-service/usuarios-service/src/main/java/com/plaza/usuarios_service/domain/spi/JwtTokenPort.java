package com.plaza.usuarios_service.domain.spi;

public interface JwtTokenPort {

    String generarToken(String correo, String rol);

    boolean validarToken(String token);

    String obtenerCorreo(String token);

    String obtenerRol(String token);
}