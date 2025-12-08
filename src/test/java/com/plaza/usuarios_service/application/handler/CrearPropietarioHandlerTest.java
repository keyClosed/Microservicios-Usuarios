package com.plaza.usuarios_service.application.handler;

import com.plaza.usuarios_service.application.dto.request.CrearPropietarioRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.usecase.CrearPropietarioUseCase;
import org.junit.jupiter.api.BeforeEach;
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
    private CrearPropietarioUseCase useCase;  // Mock del Caso de Uso

    @InjectMocks
    private CrearPropietarioHandler handler;  // Handler real que será probado

    private CrearPropietarioRequest request;


    private LocalDate generarFechaNacimientoMayorEdad() {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaMaxima = hoy.minusYears(18); // mínimo 18 años
        LocalDate fechaMinima = hoy.minusYears(100); // máximo 100 años
        long diaAleatorio = ThreadLocalRandom.current().nextLong(fechaMinima.toEpochDay(), fechaMaxima.toEpochDay());
        return LocalDate.ofEpochDay(diaAleatorio);
    }

    // Método auxiliar para generar clave aleatoria de entre 4 y 8 dígitos
    private String generarClaveAleatoria() {
        int longitud = 4 + (int)(Math.random() * 5); // 4 a 8 dígitos
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            clave.append((int)(Math.random() * 10));
        }
        return clave.toString();
    }

    // Método auxiliar para crear un request con datos aleatorios
    private CrearPropietarioRequest generarRequestAleatorio() {
        CrearPropietarioRequest r = new CrearPropietarioRequest();
        r.setNombre("Nombre_" + UUID.randomUUID().toString().substring(0,5));
        r.setApellido("Apellido_" + UUID.randomUUID().toString().substring(0,5));
        r.setDocumentoIdentidad(String.valueOf(10000000 + (int)(Math.random() * 90000000))); // 8 dígitos
        r.setCorreo("correo" + UUID.randomUUID().toString().substring(0,5) + "@mail.com");
        r.setClave(generarClaveAleatoria());
        r.setCelular("+57" + (300000000 + (int)(Math.random() * 700000000))); // formato +57xxxxxxxxx
        r.setFechaNacimiento(generarFechaNacimientoMayorEdad());
        return r;
    }

    // ==============================
    // Test de éxito: El caso de uso se ejecuta correctamente con datos aleatorios
    // ==============================
    @Test
    void debeCrearUsuarioCorrectamenteConDatosAleatorios() {
        // Arrange: request aleatorio
        request = generarRequestAleatorio();

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setNombre(request.getNombre());
        usuarioGuardado.setApellido(request.getApellido());
        usuarioGuardado.setDocumentoIdentidad(request.getDocumentoIdentidad());
        usuarioGuardado.setCorreo(request.getCorreo());

        when(useCase.ejecutar(any(Usuario.class))).thenReturn(usuarioGuardado);

        // Act
        UsuarioResponse response = handler.ejecutar(request);

        // Assert
        assertNotNull(response);
        assertEquals(request.getNombre(), response.getNombre());
        assertEquals(request.getApellido(), response.getApellido());
        assertEquals(request.getCorreo(), response.getCorreo());

        verify(useCase, times(1)).ejecutar(any(Usuario.class));
    }

    // ==============================
    // Test de fallo: Usuario menor de edad con datos aleatorios
    // ==============================
    @Test
    void debeLanzarExcepcionSiElUsuarioEsMenorDeEdad() {
        // request con fecha aleatoria menor de 18
        request = generarRequestAleatorio();
        request.setFechaNacimiento(LocalDate.now().minusYears(16)); // menor de edad

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> handler.ejecutar(request)
        );

        assertEquals("El usuario debe ser mayor de edad", exception.getMessage());
        verify(useCase, times(0)).ejecutar(any(Usuario.class));
    }
}