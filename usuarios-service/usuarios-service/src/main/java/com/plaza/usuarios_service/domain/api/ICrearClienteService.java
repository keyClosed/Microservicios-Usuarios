package com.plaza.usuarios_service.domain.api;

import com.plaza.usuarios_service.domain.model.Usuario;

public interface ICrearClienteService {

    Usuario ejecutar(Usuario usuario);
    Usuario obtenerPorId(Long id);
}
