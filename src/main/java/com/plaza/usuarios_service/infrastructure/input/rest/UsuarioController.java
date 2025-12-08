package com.plaza.usuarios_service.infrastructure.input.rest;

import com.plaza.usuarios_service.application.dto.request.CrearPropietarioRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.CrearPropietarioHandler;
import com.plaza.usuarios_service.domain.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


import org.springframework.validation.annotation.Validated; // Asegúrate de importar

@RestController
@RequestMapping("/usuarios")
@Validated
public class UsuarioController {

    private final CrearPropietarioHandler handler;

    public UsuarioController(CrearPropietarioHandler handler) {
        this.handler = handler;
    }

    @PostMapping
    public UsuarioResponse crearUsuario(@RequestBody @Valid CrearPropietarioRequest request) {
        return handler.ejecutar(request);
    }


}
