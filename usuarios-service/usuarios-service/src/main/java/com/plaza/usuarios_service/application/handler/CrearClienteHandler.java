package com.plaza.usuarios_service.application.handler;

import com.plaza.usuarios_service.application.dto.request.CrearClienteRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;

public interface CrearClienteHandler {

    UsuarioResponse ejecutar(CrearClienteRequest request);
}
