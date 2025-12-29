package com.plaza.usuarios_service.application.handler;

import com.plaza.usuarios_service.application.dto.request.CrearPropietarioRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.domain.api.ICrearPropietarioService;
import com.plaza.usuarios_service.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CrearPropietarioHandlerTest {

    @Mock
    private ICrearPropietarioService crearPropietarioService;

    @InjectMocks
    private CrearPropietarioHandler handler;  // Handler real

    private CrearPropietarioRequest request;

    private LocalDate generarFechaNacimientoMayorEdad() {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaMaxima = hoy.minusYears(18);
        LocalDate fechaMinima = hoy.minusYears(100);
        long diaAleatorio = ThreadLocalRandom.current()
                .nextLong(fechaMinima.toEpochDay(), fechaMaxima.toEpochDay());
        return LocalDate.ofEpochDay(diaAleatorio);
    }

    private String generarClaveAleatoria() {
        int longitud = 4 + (int)(Math.random() * 5);
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            clave.append((int)(Math.random() * 10));
        }
        return clave.toString();
    }

    private CrearPropietarioRequest generarRequestAleatorio() {
        CrearPropietarioRequest r = new CrearPropietarioRequest();
        r.setNombre("Nombre_" + UUID.randomUUID().toString().substring(0, 5));
        r.setApellido("Apellido_" + UUID.randomUUID().toString().substring(0, 5));
        r.setDocumentoIdentidad(String.valueOf(10000000 + (int)(Math.random() * 90000000)));
        r.setCorreo("correo" + UUID.randomUUID().toString().substring(0, 5) + "@mail.com");
        r.setClave(generarClaveAleatoria());
        r.setCelular("+57" + (300000000 + (int)(Math.random() * 700000000)));
        r.setFechaNacimiento(generarFechaNacimientoMayorEdad());
        return r;
    }


    @Test
    void debeCrearUsuarioCorrectamenteConDatosAleatorios() {

        request = generarRequestAleatorio();

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setNombre(request.getNombre());
        usuarioGuardado.setApellido(request.getApellido());
        usuarioGuardado.setDocumentoIdentidad(request.getDocumentoIdentidad());
        usuarioGuardado.setCorreo(request.getCorreo());

        when(crearPropietarioService.ejecutar(any(Usuario.class)))
                .thenReturn(usuarioGuardado);

        UsuarioResponse response = handler.ejecutar(request);

        assertNotNull(response);
        assertEquals(request.getNombre(), response.getNombre());
        assertEquals(request.getApellido(), response.getApellido());
        assertEquals(request.getCorreo(), response.getCorreo());

        verify(crearPropietarioService, times(1))
                .ejecutar(any(Usuario.class));
    }

    @Test
    void debeLanzarExcepcionSiElUsuarioEsMenorDeEdad() {

        request = generarRequestAleatorio();
        request.setFechaNacimiento(LocalDate.now().minusYears(16));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> handler.ejecutar(request)
        );

        assertEquals("El usuario debe ser mayor de edad", exception.getMessage());

        verify(crearPropietarioService, times(0))
                .ejecutar(any(Usuario.class));
    }
}