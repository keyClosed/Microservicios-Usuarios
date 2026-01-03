package com.plaza.usuarios_service.domain.usecase;

import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.PasswordEncoderPort;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrearClienteUseCaseTest {

    private UsuarioPersistencePort persistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private CrearClienteUseCase useCase;

    // Adaptador temporal
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

    @BeforeEach
    void setUp() {
        persistencePort = mock(UsuarioPersistencePort.class);
        passwordEncoderPort = new PasswordEncoderAdapterTemporal();
        useCase = new CrearClienteUseCase(persistencePort, passwordEncoderPort);
    }

    private String generarClaveAleatoria() {
        int longitud = 6 + (int)(Math.random() * 5);
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            clave.append((int)(Math.random() * 10));
        }
        return clave.toString();
    }

    private String generarCelularAleatorio() {
        return "+57" + (300000000 + (int)(Math.random() * 700000000));
    }

    private Usuario generarUsuarioClienteAleatorio() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Nombre_" + UUID.randomUUID().toString().substring(0, 5));
        usuario.setApellido("Apellido_" + UUID.randomUUID().toString().substring(0, 5));
        usuario.setDocumentoIdentidad(String.valueOf(10000000 + (int)(Math.random() * 90000000)));
        usuario.setCorreo("cliente_" + UUID.randomUUID().toString().substring(0, 5) + "@mail.com");
        usuario.setClave(generarClaveAleatoria());
        usuario.setCelular(generarCelularAleatorio());
        return usuario;
    }

    @Test
    void debeCrearClienteCorrectamente() {
        Usuario usuario = generarUsuarioClienteAleatorio();
        String claveOriginal = usuario.getClave();

        when(persistencePort.existeDocumento(usuario.getDocumentoIdentidad())).thenReturn(false);
        when(persistencePort.existeCorreo(usuario.getCorreo())).thenReturn(false);
        when(persistencePort.guardarUsuario(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioCreado = useCase.ejecutar(usuario);

        assertNotNull(usuarioCreado);
        assertEquals(usuario.getNombre(), usuarioCreado.getNombre());
        assertEquals("CLIENTE", usuarioCreado.getRol());

        assertNotEquals(claveOriginal, usuarioCreado.getClave());
        assertTrue(passwordEncoderPort.matches(claveOriginal, usuarioCreado.getClave()));

        verify(persistencePort, times(1)).existeDocumento(usuario.getDocumentoIdentidad());
        verify(persistencePort, times(1)).existeCorreo(usuario.getCorreo());
        verify(persistencePort, times(1)).guardarUsuario(any());
    }

    @Test
    void debeLanzarExcepcionSiDocumentoExiste() {
        Usuario usuario = generarUsuarioClienteAleatorio();

        when(persistencePort.existeDocumento(usuario.getDocumentoIdentidad())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.ejecutar(usuario)
        );

        assertEquals("El documento ya está registrado", exception.getMessage());
        verify(persistencePort, times(1)).existeDocumento(usuario.getDocumentoIdentidad());
        verify(persistencePort, times(0)).guardarUsuario(any());
    }

    @Test
    void debeLanzarExcepcionSiCorreoExiste() {
        Usuario usuario = generarUsuarioClienteAleatorio();

        when(persistencePort.existeDocumento(usuario.getDocumentoIdentidad())).thenReturn(false);
        when(persistencePort.existeCorreo(usuario.getCorreo())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.ejecutar(usuario)
        );

        assertEquals("El correo ya está registrado", exception.getMessage());
        verify(persistencePort, times(1)).existeDocumento(usuario.getDocumentoIdentidad());
        verify(persistencePort, times(1)).existeCorreo(usuario.getCorreo());
        verify(persistencePort, times(0)).guardarUsuario(any());
    }
}
