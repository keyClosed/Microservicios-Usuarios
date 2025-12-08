package com.plaza.usuarios_service.infrastructure.input.rest;

import com.plaza.usuarios_service.application.dto.request.CrearPropietarioRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.CrearPropietarioHandler;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/propietarios")
public class UsuarioAdminController {

    private final CrearPropietarioHandler handler;

    public UsuarioAdminController(CrearPropietarioHandler handler) {
        this.handler = handler;
    }

    @PostMapping
    public UsuarioResponse crearPropietario(@RequestBody @Valid CrearPropietarioRequest request) {
        return handler.ejecutar(request);
    }
}
