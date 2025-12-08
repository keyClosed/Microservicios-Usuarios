package com.plaza.usuarios_service.domain.spi;

import com.plaza.usuarios_service.domain.model.Usuario;

public interface UsuarioPersistencePort {

    boolean existeDocumento(String documento);

    boolean existeCorreo(String correo);

    Usuario guardarUsuario(Usuario usuario);

    Usuario obtenerPorCorreo(String correo);  // 🔹 NECESARIO PARA LOGIN
}