package com.plaza.usuarios_service.domain.usecase;

import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.PasswordEncoderPort;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrearEmpleadoUseCaseTest {

    private UsuarioPersistencePort persistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private CrearEmpleadoUseCase useCase;

    // Adapter temporal para simular encriptación
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
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        useCase = new CrearEmpleadoUseCase(persistencePort, passwordEncoderPort);

        // Simular comportamiento de encode para cualquier string
        when(passwordEncoderPort.encode(anyString())).thenAnswer(invocation -> "ENCODED_" + invocation.getArgument(0));
    }

    // Genera usuario aleatorio
    private Usuario generarUsuarioAleatorio() {
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        Usuario usuario = new Usuario();
        usuario.setNombre("Nombre_" + random);
        usuario.setApellido("Apellido_" + random);
        usuario.setDocumentoIdentidad(String.valueOf(ThreadLocalRandom.current().nextLong(10000000, 99999999)));
        usuario.setCorreo("user" + random + "@test.com");
        usuario.setClave(String.valueOf(ThreadLocalRandom.current().nextInt(1000, 999999))); // clave aleatoria
        usuario.setRol("EMPLEADO");
        usuario.setFechaNacimiento(LocalDate.now().minusYears(ThreadLocalRandom.current().nextInt(18, 50)));
        usuario.setCelular("+57" + (300000000 + ThreadLocalRandom.current().nextInt(700000000)));
        return usuario;
    }

    @Test
    void debeCrearEmpleadoCorrectamente() {
        Usuario usuario = generarUsuarioAleatorio();
        String claveOriginal = usuario.getClave();

        when(persistencePort.existeDocumento(usuario.getDocumentoIdentidad())).thenReturn(false);
        when(persistencePort.existeCorreo(usuario.getCorreo())).thenReturn(false);
        when(persistencePort.guardarUsuario(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioCreado = useCase.ejecutar(usuario);

        assertNotNull(usuarioCreado);
        assertEquals(usuario.getNombre(), usuarioCreado.getNombre());
        assertEquals(usuario.getApellido(), usuarioCreado.getApellido());
        assertEquals(usuario.getCorreo(), usuarioCreado.getCorreo());

        // La clave debe estar "encriptada"
        assertNotEquals(claveOriginal, usuarioCreado.getClave());
        assertTrue(usuarioCreado.getClave().startsWith("ENCODED_"));

        verify(persistencePort, times(1)).existeDocumento(usuario.getDocumentoIdentidad());
        verify(persistencePort, times(1)).existeCorreo(usuario.getCorreo());
        verify(persistencePort, times(1)).guardarUsuario(any());
        verify(passwordEncoderPort, times(1)).encode(anyString());
    }

    @Test
    void debeLanzarExcepcionSiDocumentoExiste() {
        Usuario usuario = generarUsuarioAleatorio();
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
        Usuario usuario = generarUsuarioAleatorio();
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