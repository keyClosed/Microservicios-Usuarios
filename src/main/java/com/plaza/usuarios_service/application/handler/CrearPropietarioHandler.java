package com.plaza.usuarios_service.application.handler;
import com.plaza.usuarios_service.application.dto.request.CrearPropietarioRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.mapper.UsuarioMapper;
import com.plaza.usuarios_service.domain.api.ICrearPropietarioService;
import com.plaza.usuarios_service.domain.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public class CrearPropietarioHandler {

    private final ICrearPropietarioService crearPropietarioService;

    public CrearPropietarioHandler(ICrearPropietarioService crearPropietarioService) {
        this.crearPropietarioService = crearPropietarioService;
    }

    public UsuarioResponse ejecutar(CrearPropietarioRequest request) {

        if (!request.esMayorDeEdad()) {
            throw new IllegalArgumentException("El usuario debe ser mayor de edad");
        }

        Usuario usuario = UsuarioMapper.toModel(request);

        Usuario usuarioGuardado = crearPropietarioService.ejecutar(usuario);


        return UsuarioMapper.toResponse(usuarioGuardado);
    }
}