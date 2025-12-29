package com.plaza.usuarios_service.domain.usecase;

import com.plaza.usuarios_service.domain.exception.UsuarioException;
import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.JwtTokenPort;
import com.plaza.usuarios_service.domain.spi.PasswordEncoderPort;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginUseCaseTest {

    private UsuarioPersistencePort persistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private JwtTokenPort jwtTokenPort;
    private LoginUseCase loginUseCase;

    // Adapter temporal para el password encoder
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

    // Adapter temporal para JWT
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
    }

    // Generar usuario aleatorio
    private Usuario generarUsuarioAleatorio() {
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        Usuario usuario = new Usuario();
        usuario.setNombre("Nombre_" + random);
        usuario.setApellido("Apellido_" + random);
        usuario.setDocumentoIdentidad(String.valueOf(ThreadLocalRandom.current().nextLong(10000000, 99999999)));
        usuario.setCorreo("user_" + random + "@test.com");
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

        Usuario usuarioLogueado = loginUseCase.login(usuario.getCorreo(), claveOriginal);

        assertNotNull(usuarioLogueado);
        assertEquals(usuario.getCorreo(), usuarioLogueado.getCorreo());
        assertEquals(usuario.getRol(), usuarioLogueado.getRol());
        assertTrue(passwordEncoderPort.matches(claveOriginal, usuarioLogueado.getClave()));

        verify(persistencePort).obtenerPorCorreo(usuario.getCorreo());
    }

    @Test
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        when(persistencePort.obtenerPorCorreo("noexiste@test.com")).thenReturn(null);

        UsuarioException exception = assertThrows(
                UsuarioException.class,
                () -> loginUseCase.login("noexiste@test.com", "Clave123")
        );

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void debeLanzarExcepcionSiClaveIncorrecta() {
        Usuario usuario = generarUsuarioAleatorio();
        String claveCorrecta = usuario.getClave();
        usuario.setClave(passwordEncoderPort.encode(claveCorrecta));

        when(persistencePort.obtenerPorCorreo(usuario.getCorreo())).thenReturn(usuario);

        UsuarioException exception = assertThrows(
                UsuarioException.class,
                () -> loginUseCase.login(usuario.getCorreo(), "ClaveIncorrecta")
        );

        assertEquals("Clave incorrecta", exception.getMessage());
    }
}