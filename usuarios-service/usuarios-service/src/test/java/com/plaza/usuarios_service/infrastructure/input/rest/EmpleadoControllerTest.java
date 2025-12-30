package com.plaza.usuarios_service.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.plaza.usuarios_service.application.dto.request.CrearEmpleadoRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.CrearEmpleadoHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CrearEmpleadoHandler handler;

    @InjectMocks
    private EmpleadoController empleadoController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(empleadoController)
                .setControllerAdvice(new UsuarioControllerAdvice())
                .build();
    }

    private String generarNombreAleatorio() {
        return "Nombre_" + UUID.randomUUID().toString().substring(0, 5);
    }

    private String generarApellidoAleatorio() {
        return "Apellido_" + UUID.randomUUID().toString().substring(0, 5);
    }

    private String generarDocumentoAleatorio() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(10000000, 99999999));
    }

    private String generarCorreoAleatorio() {
        return "correo_" + UUID.randomUUID().toString().substring(0, 5) + "@mail.com";
    }

    private String generarCelularAleatorio() {
        String[] operadores = {"300","320", "301", "302", "310", "311", "312", "313", "314", "315", "316", "317", "318", "319"};
        String operador = operadores[ThreadLocalRandom.current().nextInt(operadores.length)];
        StringBuilder numero = new StringBuilder("+57" + operador);
        for (int i = 0; i < 7; i++) numero.append(ThreadLocalRandom.current().nextInt(10));
        return numero.toString();
    }

    private String generarClaveAleatoria() {
        int longitud = ThreadLocalRandom.current().nextInt(4, 9);
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < longitud; i++) clave.append(ThreadLocalRandom.current().nextInt(10));
        return clave.toString();
    }

    @Test
    void debeCrearEmpleadoCorrectamente() throws Exception {
        CrearEmpleadoRequest request = new CrearEmpleadoRequest();
        request.setNombre(generarNombreAleatorio());
        request.setApellido(generarApellidoAleatorio());
        request.setDocumentoIdentidad(generarDocumentoAleatorio());
        request.setCorreo(generarCorreoAleatorio());
        request.setClave(generarClaveAleatoria());
        request.setCelular(generarCelularAleatorio());

        UsuarioResponse responseMock = new UsuarioResponse();
        responseMock.setNombre(request.getNombre());
        responseMock.setApellido(request.getApellido());
        responseMock.setCorreo(request.getCorreo());
        responseMock.setDocumentoIdentidad(request.getDocumentoIdentidad());

        // Simular handler devolviendo respuesta exitosa
        doReturn(responseMock).when(handler).crearEmpleado(any(), any());

        mockMvc.perform(post("/empleados")
                        .header("Authorization", "Bearer tokenPropietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value(request.getNombre()))
                .andExpect(jsonPath("$.apellido").value(request.getApellido()))
                .andExpect(jsonPath("$.correo").value(request.getCorreo()))
                .andExpect(jsonPath("$.documentoIdentidad").value(request.getDocumentoIdentidad()));
    }

    @Test
    void debeFallarSiNoEsPropietario() throws Exception {
        CrearEmpleadoRequest request = new CrearEmpleadoRequest();
        request.setNombre(generarNombreAleatorio());
        request.setApellido(generarApellidoAleatorio());
        request.setDocumentoIdentidad(generarDocumentoAleatorio());
        request.setCorreo(generarCorreoAleatorio());
        request.setClave(generarClaveAleatoria());
        request.setCelular(generarCelularAleatorio());

        // Simular que el handler lanza excepción si no es propietario
        doThrow(new IllegalArgumentException("Solo un propietario puede crear empleados"))
                .when(handler).crearEmpleado(any(), any());

        mockMvc.perform(post("/empleados")
                        .header("Authorization", "Bearer tokenNoPropietario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Solo un propietario puede crear empleados"));
    }


}

