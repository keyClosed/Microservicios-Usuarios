package com.plaza.usuarios_service.application.handler.Impl;

import com.plaza.usuarios_service.application.dto.request.CrearEmpleadoRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.CrearEmpleadoHandler;
import com.plaza.usuarios_service.application.mapper.UsuarioMapper;
import com.plaza.usuarios_service.domain.api.ICrearEmpleadoService;
import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.infrastructure.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class CrearEmpleadoHandlerImpl implements CrearEmpleadoHandler {

    private final ICrearEmpleadoService crearEmpleadoService;
    private final JwtUtil jwtUtil;

    public CrearEmpleadoHandlerImpl(ICrearEmpleadoService crearEmpleadoService,
                                    JwtUtil jwtUtil) {
        this.crearEmpleadoService = crearEmpleadoService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UsuarioResponse crearEmpleado(CrearEmpleadoRequest request,
                                         String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");

        String rol = jwtUtil.getRolFromToken(token);


        if (!"PROPIETARIO".equals(rol)) {
            throw new IllegalArgumentException("Solo un propietario puede crear empleados");
        }

        Usuario usuario = UsuarioMapper.toModelEmpleado(request);

        Usuario usuarioGuardado = crearEmpleadoService.ejecutar(usuario);

        return UsuarioMapper.toResponse(usuarioGuardado);
    }
}