package com.plaza.usuarios_service.domain.usecase;

import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrearPropietarioUseCaseTest {

    private UsuarioPersistencePort persistencePort;
    private BCryptPasswordEncoder passwordEncoder;
    private CrearPropietarioUseCase useCase;

    @BeforeEach
    void setUp() {
        persistencePort = mock(UsuarioPersistencePort.class);
        passwordEncoder = new BCryptPasswordEncoder();
        useCase = new CrearPropietarioUseCase(persistencePort, passwordEncoder);
    }

    private LocalDate generarFechaNacimientoMayorEdad() {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaMaxima = hoy.minusYears(18);
        LocalDate fechaMinima = hoy.minusYears(100);
        long diaAleatorio = ThreadLocalRandom.current()
                .nextLong(fechaMinima.toEpochDay(), fechaMaxima.toEpochDay());
        return LocalDate.ofEpochDay(diaAleatorio);
    }

    private Usuario generarUsuarioAleatorioSeguro() {
        Usuario usuario = new Usuario();

        usuario.setNombre("Nombre_" + UUID.randomUUID().toString().substring(0, 5));
        usuario.setApellido("Apellido_" + UUID.randomUUID().toString().substring(0, 5));
        usuario.setDocumentoIdentidad(String.valueOf((int)(Math.random() * 90000000 + 10000000)));
        usuario.setCorreo("test" + UUID.randomUUID().toString().substring(0, 5) + "@test.com");
        usuario.setClave("Clave123");
        usuario.setCelular("+573001112233");
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

        // La clave debe estar encriptada
        assertNotEquals(claveOriginal, usuarioCreado.getClave());
        assertTrue(passwordEncoder.matches(claveOriginal, usuarioCreado.getClave()));

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
}