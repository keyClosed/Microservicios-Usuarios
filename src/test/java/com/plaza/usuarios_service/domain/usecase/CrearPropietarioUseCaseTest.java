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

    // Genera fecha de nacimiento de mayor de edad
    private LocalDate generarFechaNacimientoMayorEdad() {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaMaxima = hoy.minusYears(18);
        LocalDate fechaMinima = hoy.minusYears(100);
        long diaAleatorio = ThreadLocalRandom.current().nextLong(fechaMinima.toEpochDay(), fechaMaxima.toEpochDay());
        return LocalDate.ofEpochDay(diaAleatorio);
    }

    // Genera usuario aleatorio seguro, con contraseña alfanumérica
    private Usuario generarUsuarioAleatorioSeguro() {
        Usuario usuario = new Usuario();

        // Nombres y apellidos aleatorios
        usuario.setNombre("Nombre_" + UUID.randomUUID().toString().substring(0, 5));
        usuario.setApellido("Apellido_" + UUID.randomUUID().toString().substring(0, 5));

        // Documento de identidad de 8 dígitos
        usuario.setDocumentoIdentidad(String.valueOf((int)(Math.random() * 90000000 + 10000000)));

        // Correo aleatorio
        usuario.setCorreo("test" + UUID.randomUUID().toString().substring(0, 5) + "@test.com");

        // Clave segura: alfanumérica de 8 caracteres
        usuario.setClave(generarClaveAlfanumerica(8));

        // Celular válido en Colombia
        usuario.setCelular("+57" + ((int)(Math.random() * 900000000 + 100000000)));

        // Fecha de nacimiento mayor de edad
        usuario.setFechaNacimiento(generarFechaNacimientoMayorEdad());

        return usuario;
    }

    // Genera una cadena alfanumérica de longitud dada
    private String generarClaveAlfanumerica(int longitud) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

    @Test
    void debeCrearUsuarioCorrectamente() {
        // Arrange
        Usuario usuario = generarUsuarioAleatorioSeguro();
        String claveOriginal = usuario.getClave(); // ← guardamos la contraseña original antes de encriptar

        // Simulamos que el documento no existe
        when(persistencePort.existeDocumento(usuario.getDocumentoIdentidad())).thenReturn(false);

        // Simulamos guardar usuario
        when(persistencePort.guardarUsuario(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Usuario usuarioCreado = useCase.ejecutar(usuario);

        // Assert
        assertNotNull(usuarioCreado);
        assertEquals(usuario.getNombre(), usuarioCreado.getNombre());
        assertNotEquals(claveOriginal, usuarioCreado.getClave());  // Clave debe estar encriptada
        assertTrue(passwordEncoder.matches(claveOriginal, usuarioCreado.getClave())); // Verificar encriptación

        // Verificamos las llamadas al persistencePort
        verify(persistencePort, times(1)).existeDocumento(usuario.getDocumentoIdentidad());
        verify(persistencePort, times(1)).guardarUsuario(any());
    }

    @Test
    void debeLanzarExcepcionSiDocumentoExiste() {
        // Arrange
        Usuario usuario = generarUsuarioAleatorioSeguro();

        // Simulamos que el documento ya existe
        when(persistencePort.existeDocumento(usuario.getDocumentoIdentidad())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.ejecutar(usuario)
        );

        assertEquals("El documento ya está registrado", exception.getMessage());
        verify(persistencePort, times(1)).existeDocumento(usuario.getDocumentoIdentidad());
        verify(persistencePort, times(0)).guardarUsuario(any());
    }
}