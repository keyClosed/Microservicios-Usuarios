package com.plaza.usuarios_service.application.handler;

import com.plaza.usuarios_service.application.dto.request.CrearClienteRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.Impl.CrearClienteHandlerImpl;
import com.plaza.usuarios_service.domain.api.ICrearClienteService;

import com.plaza.usuarios_service.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearClienteHandlerTest {

    @Mock
    private ICrearClienteService crearClienteService;

    @InjectMocks
    private CrearClienteHandlerImpl handler;

    private String generarClaveAleatoria() {
        int longitud = 6 + ThreadLocalRandom.current().nextInt(5);
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            clave.append(ThreadLocalRandom.current().nextInt(10));
        }
        return clave.toString();
    }

    private CrearClienteRequest generarRequestAleatorio() {
        CrearClienteRequest r = new CrearClienteRequest();
        r.setNombre("Nombre_" + UUID.randomUUID().toString().substring(0, 5));
        r.setApellido("Apellido_" + UUID.randomUUID().toString().substring(0, 5));
        r.setDocumentoIdentidad(String.valueOf(
                10000000 + ThreadLocalRandom.current().nextInt(90000000)
        ));
        r.setCorreo("correo" + UUID.randomUUID().toString().substring(0, 5) + "@mail.com");
        r.setClave(generarClaveAleatoria());
        r.setCelular("+57" + (300000000 + ThreadLocalRandom.current().nextInt(700000000)));
        return r;
    }

    @Test
    void debeCrearClienteCorrectamente() {

        CrearClienteRequest request = generarRequestAleatorio();

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(1L);
        usuarioGuardado.setNombre(request.getNombre());
        usuarioGuardado.setApellido(request.getApellido());
        usuarioGuardado.setDocumentoIdentidad(request.getDocumentoIdentidad());
        usuarioGuardado.setCorreo(request.getCorreo());
        usuarioGuardado.setRol("CLIENTE");

        when(crearClienteService.ejecutar(any(Usuario.class)))
                .thenReturn(usuarioGuardado);

        UsuarioResponse response = handler.ejecutar(request);

        assertNotNull(response);
        assertEquals(request.getNombre(), response.getNombre());
        assertEquals(request.getApellido(), response.getApellido());
        assertEquals(request.getCorreo(), response.getCorreo());
        assertEquals("CLIENTE", response.getRol());

        verify(crearClienteService, times(1))
                .ejecutar(any(Usuario.class));
    }
}