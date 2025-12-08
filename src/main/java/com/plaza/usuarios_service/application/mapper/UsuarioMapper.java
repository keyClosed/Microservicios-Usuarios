package com.plaza.usuarios_service.application.mapper;

import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.application.dto.request.CrearPropietarioRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;

public class UsuarioMapper {

    // Request -> Modelo de dominio
    public static Usuario toModel(CrearPropietarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setDocumentoIdentidad(request.getDocumentoIdentidad());
        usuario.setCelular(request.getCelular());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setCorreo(request.getCorreo());
        usuario.setClave(request.getClave());
        usuario.setRol("PROPIETARIO");
        return usuario;
    }

    // Modelo de dominio -> Response
    public static UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setDocumentoIdentidad(usuario.getDocumentoIdentidad());
        response.setCelular(usuario.getCelular());
        response.setFechaNacimiento(usuario.getFechaNacimiento());
        response.setCorreo(usuario.getCorreo());
        response.setRol(usuario.getRol());
        return response;
    }
}
