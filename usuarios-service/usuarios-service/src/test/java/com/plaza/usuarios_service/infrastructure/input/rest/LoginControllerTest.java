package com.plaza.usuarios_service.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plaza.usuarios_service.application.dto.request.LoginRequest;
import com.plaza.usuarios_service.application.dto.response.LoginResponse;
import com.plaza.usuarios_service.application.handler.Impl.LoginHandlerImpl;
import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.JwtTokenPort;
import com.plaza.usuarios_service.domain.spi.PasswordEncoderPort;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import com.plaza.usuarios_service.domain.usecase.LoginUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoginControllerTest {

    private MockMvc mockMvc;
    private UsuarioPersistencePort persistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private JwtTokenPort jwtTokenPort;
    private ObjectMapper objectMapper;

    static class PasswordEncoderAdapterTemporal implements PasswordEncoderPort {
        @Override
        public String encode(String rawPassword) {
            return "ENCODED_" + rawPassword;
        }
        @Override
        public boolean matches(String rawPassword, String encodedPassword) {
            return encodedPassword.equals("ENCODED_" + rawPassword);
        }
    }

    static class JwtTokenAdapterTemporal implements JwtTokenPort {
        @Override
        public String generarToken(String correo, String rol) {
            return "TOKEN_" + correo + "_" + rol;
        }
        @Override
        public boolean validarToken(String token) {
            return token != null && token.startsWith("TOKEN_");
        }
        @Override
        public String obtenerCorreo(String token) { return token.split("_")[1]; }
        @Override
        public String obtenerRol(String token) { return token.split("_")[2]; }
    }

    @BeforeEach
    void setUp() {
        persistencePort = mock(UsuarioPersistencePort.class);
        passwordEncoderPort = new PasswordEncoderAdapterTemporal();
        jwtTokenPort = new JwtTokenAdapterTemporal();

        LoginUseCase loginUseCase = new LoginUseCase(persistencePort, passwordEncoderPort, jwtTokenPort);
        LoginHandlerImpl loginHandler = new LoginHandlerImpl(loginUseCase, jwtTokenPort);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new LoginController(loginHandler))
                .build();

        objectMapper = new ObjectMapper();
    }


    private Usuario generarUsuarioAleatorio() {
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        Usuario usuario = new Usuario();
        usuario.setNombre("Nombre" + random);
        usuario.setApellido("Apellido" + random);
        usuario.setDocumentoIdentidad(String.valueOf(ThreadLocalRandom.current().nextLong(10000000, 99999999)));
        usuario.setCorreo("user" + random + "@test.com");
        usuario.setClave("Clave" + random);
        usuario.setRol(random % 2 == 0 ? "PROPIETARIO" : "CLIENTE");
        usuario.setFechaNacimiento(LocalDate.now().minusYears(ThreadLocalRandom.current().nextInt(18, 50)));
        return usuario;
    }

    @Test
    void debeLoguearUsuarioConEndpoint() throws Exception {
        Usuario usuario = generarUsuarioAleatorio();
        String claveOriginal = usuario.getClave();
        usuario.setClave(passwordEncoderPort.encode(claveOriginal));

        when(persistencePort.obtenerPorCorreo(usuario.getCorreo())).thenReturn(usuario);

        LoginRequest request = new LoginRequest();
        request.setCorreo(usuario.getCorreo());
        request.setClave(claveOriginal);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value(usuario.getNombre()))
                .andExpect(jsonPath("$.rol").value(usuario.getRol()))
                .andExpect(jsonPath("$.token")
                        .value("TOKEN_" + usuario.getCorreo() + "_" + usuario.getRol()));

        verify(persistencePort, times(1)).obtenerPorCorreo(usuario.getCorreo());
    }
}