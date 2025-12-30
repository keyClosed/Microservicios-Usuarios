package com.plaza.usuarios_service.application.handler;

import com.plaza.usuarios_service.application.dto.request.CrearEmpleadoRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.Impl.CrearEmpleadoHandlerImpl;
import com.plaza.usuarios_service.domain.api.ICrearEmpleadoService;
import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.infrastructure.security.JwtUtil;
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
class CrearEmpleadoHandlerTest {

    @Mock
    private ICrearEmpleadoService crearEmpleadoService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private CrearEmpleadoHandlerImpl handler; // <-- clase concreta

    // Generar request aleatorio para empleado
    private CrearEmpleadoRequest generarRequestAleatorio() {
        CrearEmpleadoRequest r = new CrearEmpleadoRequest();

        r.setNombre("Nombre_" + UUID.randomUUID().toString().substring(0, 5));
        r.setApellido("Apellido_" + UUID.randomUUID().toString().substring(0, 5));
        r.setDocumentoIdentidad(String.valueOf(ThreadLocalRandom.current().nextLong(10000000, 99999999)));
        r.setCorreo("correo_" + UUID.randomUUID().toString().substring(0, 5) + "@gmail.com");
        r.setClave(generarClaveAleatoria());          // clave aleatoria
        r.setCelular(generarCelularAleatorio());      // celular aleatorio

        return r;
    }

    private String generarClaveAleatoria() {
        int longitud = 4 + (int)(Math.random() * 5); // 4 a 8 dígitos
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            clave.append((int)(Math.random() * 10));
        }
        return clave.toString();
    }

    private String generarCelularAleatorio() {
        return "+57" + (300000000 + (int)(Math.random() * 700000000));
    }

    @Test
    void debeCrearEmpleadoSiEsPropietario() {

        CrearEmpleadoRequest request = generarRequestAleatorio();
        String token = "tokenValido";

        when(jwtUtil.getRolFromToken(token)).thenReturn("PROPIETARIO");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setNombre(request.getNombre());
        usuarioGuardado.setApellido(request.getApellido());
        usuarioGuardado.setCorreo(request.getCorreo());

        when(crearEmpleadoService.ejecutar(any(Usuario.class))).thenReturn(usuarioGuardado);

        UsuarioResponse response = handler.crearEmpleado(request, "Bearer " + token);

        assertNotNull(response);
        assertEquals(request.getNombre(), response.getNombre());
        assertEquals(request.getApellido(), response.getApellido());
        assertEquals(request.getCorreo(), response.getCorreo());

        verify(crearEmpleadoService, times(1)).ejecutar(any(Usuario.class));
        verify(jwtUtil, times(1)).getRolFromToken(token);
    }

    @Test
    void debeLanzarExcepcionSiNoEsPropietario() {

        CrearEmpleadoRequest request = generarRequestAleatorio();
        String token = "tokenNoPropietario";

        when(jwtUtil.getRolFromToken(token)).thenReturn("EMPLEADO");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> handler.crearEmpleado(request, "Bearer " + token)
        );

        assertEquals("Solo un propietario puede crear empleados", exception.getMessage());

        verify(crearEmpleadoService, times(0)).ejecutar(any());
        verify(jwtUtil, times(1)).getRolFromToken(token);
    }
}
