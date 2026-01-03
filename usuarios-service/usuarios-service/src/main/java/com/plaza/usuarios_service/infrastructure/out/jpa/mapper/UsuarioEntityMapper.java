package com.plaza.usuarios_service.infrastructure.out.jpa.mapper;

import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.infrastructure.out.jpa.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEntityMapper {

    public UsuarioEntity toEntity(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.getId());
        entity.setNombre(usuario.getNombre());
        entity.setApellido(usuario.getApellido());
        entity.setDocumentoIdentidad(usuario.getDocumentoIdentidad());
        entity.setCelular(usuario.getCelular());
        entity.setFechaNacimiento(usuario.getFechaNacimiento());
        entity.setCorreo(usuario.getCorreo());
        entity.setClave(usuario.getClave());
        entity.setRol(usuario.getRol());
        return entity;
    }

    public Usuario toDomain(UsuarioEntity entity) {
        Usuario usuario = new Usuario();
        usuario.setId(entity.getId());
        usuario.setNombre(entity.getNombre());
        usuario.setApellido(entity.getApellido());
        usuario.setDocumentoIdentidad(entity.getDocumentoIdentidad());
        usuario.setCelular(entity.getCelular());
        usuario.setFechaNacimiento(entity.getFechaNacimiento());
        usuario.setCorreo(entity.getCorreo());
        usuario.setClave(entity.getClave());
        usuario.setRol(entity.getRol());
        return usuario;
    }
}