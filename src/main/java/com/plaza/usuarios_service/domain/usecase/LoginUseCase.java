package com.plaza.usuarios_service.domain.usecase;

import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class LoginUseCase {

    private final UsuarioPersistencePort persistencePort;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginUseCase(UsuarioPersistencePort persistencePort,
                        BCryptPasswordEncoder passwordEncoder) {
        this.persistencePort = persistencePort;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario login(String correo, String clave) {

        // Buscar usuario por correo
        Usuario usuario = persistencePort.obtenerPorCorreo(correo);

        // Validar existencia
        if (usuario == null) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos");
        }

        // Validar contraseña con BCrypt
        if (!passwordEncoder.matches(clave, usuario.getClave())) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos");
        }

        // Login exitoso
        return usuario;
    }
}