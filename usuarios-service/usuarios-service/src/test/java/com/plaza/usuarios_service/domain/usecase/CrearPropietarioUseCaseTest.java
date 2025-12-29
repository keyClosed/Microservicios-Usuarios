package com.plaza.usuarios_service.domain.usecase;

import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import com.plaza.usuarios_service.domain.spi.PasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrearPropietarioUseCaseTest {

    private UsuarioPersistencePort persistencePort;
    private PasswordEncoderPort passwordEncoderPort;
    private CrearPropietarioUseCase useCase;

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
        useCase = new CrearPropietarioUseCase(persistencePort, passwordEncoderPort);
    }

    private LocalDate generarFechaNacimientoMayorEdad() {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaMaxima = hoy.minusYears(18);
        LocalDate fechaMinima = hoy.minusYears(100);
        long diaAleatorio = ThreadLocalRandom.current()
                .nextLong(fechaMinima.toEpochDay(), fechaMaxima.toEpochDay());
        return LocalDate.ofEpochDay(diaAleatorio);
    }

    private String generarClaveAleatoria() {
        int longitud = 4 + (int)(Math.random() * 5); // entre 4 y 8 dígitos
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            clave.append((int)(Math.random() * 10));
        }
        return clave.toString();
    }

    private String generarCelularAleatorio() {
        return "+57" + (300000000 + (int)(Math.random() * 700000000));
    }


    private Usuario generarUsuarioAleatorioSeguro() {
        Usuario usuario = new Usuario();

        usuario.setNombre("Nombre_" + UUID.randomUUID().toString().substring(0, 5));
        usuario.setApellido("Apellido_" + UUID.randomUUID().toString().substring(0, 5));
        usuario.setDocumentoIdentidad(String.valueOf((int)(Math.random() * 90000000 + 10000000)));
        usuario.setCorreo("test" + UUID.randomUUID().toString().substring(0, 5) + "@gmail.com");
        usuario.setClave(generarClaveAleatoria());
        // Adaptador temporal para simular el encriptado
        usuario.setCelular(generarCelularAleatorio());
        usuario.setFechaNacimiento(generarFechaNacimientoMayorEdad());

        return usuario;
    }

    @Test
    void debeCrearUsuarioCorrectamente() {
        Usuario usuario = generarUsuarioAleatorioSeguro();
        String claveOriginal = usuario.getClave();

        when(persistencePort.existeDocumento(usuario.getDocumentoIdentidad())).thenReturn(false);
        when(persistencePort.existeCorreo(usuario.getCorreo())).thenReturn(false);
        when(persistencePort.guardarUsuario(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioCreado = useCase.ejecutar(usuario);

        assertNotNull(usuarioCreado);
        assertEquals(usuario.getNombre(), usuarioCreado.getNombre());

        assertNotEquals(claveOriginal, usuarioCreado.getClave());
        assertTrue(passwordEncoderPort.matches(claveOriginal, usuarioCreado.getClave()));

        verify(persistencePort, times(1)).existeDocumento(usuario.getDocumentoIdentidad());
        verify(persistencePort, times(1)).existeCorreo(usuario.getCorreo());
        verify(persistencePort, times(1)).guardarUsuario(any());
    }

    @Test
    void debeLanzarExcepcionSiDocumentoExiste() {
        Usuario usuario = generarUsuarioAleatorioSeguro();

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
        Usuario usuario = generarUsuarioAleatorioSeguro();

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