package com.plaza.usuarios_service.application.handler.Impl;

import com.plaza.usuarios_service.application.dto.request.CrearClienteRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.CrearClienteHandler;
import com.plaza.usuarios_service.application.mapper.UsuarioMapper;
import com.plaza.usuarios_service.domain.api.ICrearClienteService;
import com.plaza.usuarios_service.domain.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public class CrearClienteHandlerImpl implements CrearClienteHandler {

    private final ICrearClienteService crearClienteService;

    public CrearClienteHandlerImpl(ICrearClienteService crearClienteService) {
        this.crearClienteService = crearClienteService;
    }

    @Override
    public UsuarioResponse ejecutar(CrearClienteRequest request) {

        Usuario usuario = UsuarioMapper.toModel(request);

        Usuario usuarioGuardado = crearClienteService.ejecutar(usuario);

        return UsuarioMapper.toResponse(usuarioGuardado);
    }
}