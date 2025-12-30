package com.plaza.usuarios_service.infrastructure.input.rest;


import com.plaza.usuarios_service.application.dto.request.CrearEmpleadoRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.CrearEmpleadoHandler;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empleados")
@Validated
public class EmpleadoController {

    private final CrearEmpleadoHandler empleadoHandler;

    public EmpleadoController(CrearEmpleadoHandler empleadoHandler) {
        this.empleadoHandler = empleadoHandler;
    }

    @PostMapping
    public UsuarioResponse crearEmpleado(@RequestBody @Valid CrearEmpleadoRequest request,
                                         @RequestHeader("Authorization") String authHeader) {
        return empleadoHandler.crearEmpleado(request, authHeader);
    }
}