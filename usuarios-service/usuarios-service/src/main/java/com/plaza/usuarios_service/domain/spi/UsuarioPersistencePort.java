package com.plaza.usuarios_service.domain.spi;

import com.plaza.usuarios_service.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioPersistencePort {

    boolean existeDocumento(String documento);

    boolean existeCorreo(String correo);

    Usuario guardarUsuario(Usuario usuario);

    Usuario obtenerPorCorreo(String correo);
    Usuario obtenerPorId(Long id);//

}