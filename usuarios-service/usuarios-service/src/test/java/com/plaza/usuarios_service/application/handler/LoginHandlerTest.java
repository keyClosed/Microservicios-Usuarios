package com.plaza.usuarios_service.application.handler;

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

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginHandlerTest {

    private UsuarioPersistencePort persistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private JwtTokenPort jwtTokenPort;
    private LoginUseCase loginUseCase;
    private LoginHandlerImpl loginHandler;

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
        public String obtenerCorreo(String token) {
            return token.split("_")[1];
        }

        @Override
        public String obtenerRol(String token) {
            return token.split("_")[2];
        }
    }

    @BeforeEach
    void setUp() {
        persistencePort = mock(UsuarioPersistencePort.class);
        passwordEncoderPort = new PasswordEncoderAdapterTemporal();
        jwtTokenPort = new JwtTokenAdapterTemporal();

        loginUseCase = new LoginUseCase(persistencePort, passwordEncoderPort, jwtTokenPort);
        loginHandler = new LoginHandlerImpl(loginUseCase, jwtTokenPort);
    }

    private Usuario generarUsuarioAleatorio() {
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        Usuario usuario = new Usuario();
        usuario.setNombre("Nombre" + random);
        usuario.setApellido("Apellido" + random);
        usuario.setDocumentoIdentidad(String.valueOf(ThreadLocalRandom.current().nextLong(10000000, 99999999)));
        usuario.setCorreo("user" + random + "@gmail.com");
        usuario.setClave("Clave" + random);
        usuario.setRol(random % 2 == 0 ? "PROPIETARIO" : "CLIENTE");
        usuario.setFechaNacimiento(LocalDate.now().minusYears(ThreadLocalRandom.current().nextInt(18, 50)));
        return usuario;
    }

    @Test
    void debeLoguearUsuarioCorrectamente() {
        Usuario usuario = generarUsuarioAleatorio();
        String claveOriginal = usuario.getClave();
        usuario.setClave(passwordEncoderPort.encode(claveOriginal));

        when(persistencePort.obtenerPorCorreo(usuario.getCorreo())).thenReturn(usuario);

        LoginRequest request = new LoginRequest();
        request.setCorreo(usuario.getCorreo());
        request.setClave(claveOriginal);

        LoginResponse response = loginHandler.login(request);

        assertNotNull(response);
        assertEquals(usuario.getNombre(), response.getNombre());
        assertEquals(usuario.getRol(), response.getRol());
        assertEquals(
                "TOKEN_" + usuario.getCorreo() + "_" + usuario.getRol(),
                response.getToken()
        );

        verify(persistencePort).obtenerPorCorreo(usuario.getCorreo());
    }
}