package com.plaza.usuarios_service.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plaza.usuarios_service.application.dto.request.CrearClienteRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.CrearClienteHandler;
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
class ClienteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CrearClienteHandler handler;

    @InjectMocks
    private ClienteController clienteController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
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
        return String.valueOf((int) (Math.random() * 90000000 + 10000000));
    }

    private String generarCorreoAleatorio() {
        return "cliente" + UUID.randomUUID().toString().substring(0, 5) + "@mail.com";
    }

    private String generarCelularValido() {
        String[] operadores = {"300", "301", "302", "310", "311", "312", "313", "314", "315"};
        String operador = operadores[(int) (Math.random() * operadores.length)];
        StringBuilder numero = new StringBuilder("+57" + operador);
        for (int i = 0; i < 7; i++) numero.append((int) (Math.random() * 10));
        return numero.toString();
    }

    private String generarClaveAleatoria() {
        int longitud = ThreadLocalRandom.current().nextInt(6, 10);
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < longitud; i++) clave.append((int) (Math.random() * 10));
        return clave.toString();
    }

    @Test
    void debeCrearClienteCorrectamente() throws Exception {
        CrearClienteRequest request = new CrearClienteRequest();
        request.setNombre(generarNombreAleatorio());
        request.setApellido(generarApellidoAleatorio());
        request.setDocumentoIdentidad(generarDocumentoAleatorio());
        request.setCorreo(generarCorreoAleatorio());
        request.setClave(generarClaveAleatoria());
        request.setCelular(generarCelularValido());

        UsuarioResponse responseMock = new UsuarioResponse();
        responseMock.setNombre(request.getNombre());
        responseMock.setApellido(request.getApellido());
        responseMock.setDocumentoIdentidad(request.getDocumentoIdentidad());
        responseMock.setCorreo(request.getCorreo());
        responseMock.setRol("CLIENTE");

        doReturn(responseMock).when(handler).ejecutar(any());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value(request.getNombre()))
                .andExpect(jsonPath("$.apellido").value(request.getApellido()))
                .andExpect(jsonPath("$.correo").value(request.getCorreo()))
                .andExpect(jsonPath("$.documentoIdentidad").value(request.getDocumentoIdentidad()))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void debeRetornarBadRequestSiDocumentoDuplicado() throws Exception {
        CrearClienteRequest request = new CrearClienteRequest();
        request.setNombre(generarNombreAleatorio());
        request.setApellido(generarApellidoAleatorio());
        request.setDocumentoIdentidad(generarDocumentoAleatorio());
        request.setCorreo(generarCorreoAleatorio());
        request.setClave(generarClaveAleatoria());
        request.setCelular(generarCelularValido());

        doThrow(new IllegalArgumentException("El documento ya está registrado"))
                .when(handler).ejecutar(any());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El documento ya está registrado"));
    }

    @Test
    void debeRetornarBadRequestSiCorreoDuplicado() throws Exception {
        CrearClienteRequest request = new CrearClienteRequest();
        request.setNombre(generarNombreAleatorio());
        request.setApellido(generarApellidoAleatorio());
        request.setDocumentoIdentidad(generarDocumentoAleatorio());
        request.setCorreo(generarCorreoAleatorio());
        request.setClave(generarClaveAleatoria());
        request.setCelular(generarCelularValido());

        doThrow(new IllegalArgumentException("El correo ya está registrado"))
                .when(handler).ejecutar(any());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El correo ya está registrado"));
    }
}