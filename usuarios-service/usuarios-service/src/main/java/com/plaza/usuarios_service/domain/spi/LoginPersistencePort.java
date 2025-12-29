package com.plaza.usuarios_service.domain.spi;

import com.plaza.usuarios_service.domain.model.Usuario;

import java.util.Optional;

public interface LoginPersistencePort {


    Optional<Usuario> obtenerUsuarioPorCorreo(String correo);

    void guardarToken(String token, String correoUsuario);

    boolean validarToken(String token);



}
