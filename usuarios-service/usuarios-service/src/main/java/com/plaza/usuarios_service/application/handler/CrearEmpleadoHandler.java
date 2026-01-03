package com.plaza.usuarios_service.application.handler;

import com.plaza.usuarios_service.application.dto.request.CrearEmpleadoRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;

public interface CrearEmpleadoHandler {

    UsuarioResponse crearEmpleado(CrearEmpleadoRequest request, String authorizationHeader);
}
