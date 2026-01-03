package com.plaza.usuarios_service.infrastructure.input.rest;

import com.plaza.usuarios_service.application.dto.request.CrearClienteRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.CrearClienteHandler;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@Validated
public class ClienteController {

    private final CrearClienteHandler crearClienteHandler;

    public ClienteController(CrearClienteHandler crearClienteHandler) {
        this.crearClienteHandler = crearClienteHandler;
    }

    @PostMapping
    public UsuarioResponse crearCliente(
            @RequestBody @Valid CrearClienteRequest request
    ) {
        return crearClienteHandler.ejecutar(request);
    }
}
