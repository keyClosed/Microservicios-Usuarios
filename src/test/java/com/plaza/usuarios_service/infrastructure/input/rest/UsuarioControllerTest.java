package com.plaza.usuarios_service.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.plaza.usuarios_service.application.dto.request.CrearPropietarioRequest;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
import com.plaza.usuarios_service.application.handler.CrearPropietarioHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CrearPropietarioHandler handler;

    @InjectMocks
    private UsuarioController usuarioController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Registramos JavaTimeModule para que Jackson maneje LocalDate
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new UsuarioControllerAdvice()) // tu clase de manejo de excepciones
                .build();
    }

    // ==============================
    // Métodos auxiliares
    // ==============================
    private String generarNombreAleatorio() {
        return "Nombre_" + UUID.randomUUID().toString().substring(0, 5);
    }

    private String generarApellidoAleatorio() {
        return "Apellido_" + UUID.randomUUID().toString().substring(0, 5);
    }

    private String generarDocumentoAleatorio() {
        return String.valueOf((int)(Math.random() * 90000000 + 10000000));
    }

    private String generarCorreoAleatorio() {
        return "test" + UUID.randomUUID().toString().substring(0, 5) + "@mail.com";
    }

    private String generarCelularValido() {
        String[] operadores = {"300","301","302","310","311","312","313","314","315","316","317","318","319"};
        String operador = operadores[(int)(Math.random() * operadores.length)];
        StringBuilder numero = new StringBuilder("+57" + operador);
        for (int i = 0; i < 7; i++) numero.append((int)(Math.random() * 10));
        return numero.toString();
    }

    private String generarClaveAleatoria() {
        int longitud = ThreadLocalRandom.current().nextInt(4, 9);
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < longitud; i++) clave.append((int)(Math.random() * 10));
        return clave.toString();
    }

    // ==============================
    // Test creación exitosa
    // ==============================
    @Test
    void debeCrearUsuarioCorrectamente() throws Exception {
        CrearPropietarioRequest request = new CrearPropietarioRequest();
        request.setNombre(generarNombreAleatorio());
        request.setApellido(generarApellidoAleatorio());
        request.setDocumentoIdentidad(generarDocumentoAleatorio());
        request.setCorreo(generarCorreoAleatorio());
        request.setClave(generarClaveAleatoria());
        request.setCelular(generarCelularValido());
        request.setFechaNacimiento(LocalDate.now().minusYears(25));

        UsuarioResponse responseMock = new UsuarioResponse();
        responseMock.setNombre(request.getNombre());
        responseMock.setApellido(request.getApellido());
        responseMock.setDocumentoIdentidad(request.getDocumentoIdentidad());
        responseMock.setCorreo(request.getCorreo());

        doReturn(responseMock).when(handler).ejecutar(any(CrearPropietarioRequest.class));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value(request.getNombre()))
                .andExpect(jsonPath("$.apellido").value(request.getApellido()))
                .andExpect(jsonPath("$.correo").value(request.getCorreo()))
                .andExpect(jsonPath("$.documentoIdentidad").value(request.getDocumentoIdentidad()));
    }

    // ==============================
    // Test menor de edad
    // ==============================
    @Test
    void debeRetornarBadRequestSiElUsuarioEsMenorDeEdad() throws Exception {
        CrearPropietarioRequest request = new CrearPropietarioRequest();
        request.setNombre(generarNombreAleatorio());
        request.setApellido(generarApellidoAleatorio());
        request.setDocumentoIdentidad(generarDocumentoAleatorio());
        request.setCorreo(generarCorreoAleatorio());
        request.setClave(generarClaveAleatoria());
        request.setCelular(generarCelularValido());
        request.setFechaNacimiento(LocalDate.now().minusYears(16));

        doThrow(new IllegalArgumentException("El usuario debe ser mayor de edad"))
                .when(handler).ejecutar(any(CrearPropietarioRequest.class));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El usuario debe ser mayor de edad"));
    }

    // ==============================
    // Test documento duplicado
    // ==============================
    @Test
    void debeRetornarBadRequestSiDocumentoDuplicado() throws Exception {
        String documentoDuplicado = generarDocumentoAleatorio();

        CrearPropietarioRequest request1 = new CrearPropietarioRequest();
        request1.setNombre(generarNombreAleatorio());
        request1.setApellido(generarApellidoAleatorio());
        request1.setDocumentoIdentidad(documentoDuplicado);
        request1.setCorreo(generarCorreoAleatorio());
        request1.setClave(generarClaveAleatoria());
        request1.setCelular(generarCelularValido());
        request1.setFechaNacimiento(LocalDate.now().minusYears(25));

        CrearPropietarioRequest request2 = new CrearPropietarioRequest();
        request2.setNombre(generarNombreAleatorio());
        request2.setApellido(generarApellidoAleatorio());
        request2.setDocumentoIdentidad(documentoDuplicado);
        request2.setCorreo(generarCorreoAleatorio());
        request2.setClave(generarClaveAleatoria());
        request2.setCelular(generarCelularValido());
        request2.setFechaNacimiento(LocalDate.now().minusYears(25));

        UsuarioResponse responseMock = new UsuarioResponse();
        responseMock.setNombre(request1.getNombre());
        responseMock.setApellido(request1.getApellido());
        responseMock.setDocumentoIdentidad(request1.getDocumentoIdentidad());
        responseMock.setCorreo(request1.getCorreo());

        // Primera llamada OK, segunda llamada excepción
        doReturn(responseMock)
                .doThrow(new IllegalArgumentException("Documento duplicado"))
                .when(handler).ejecutar(any(CrearPropietarioRequest.class));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Documento duplicado"));
    }
}