package com.plaza.usuarios_service.domain.usecase;

import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CrearPropietarioUseCase {

    private final UsuarioPersistencePort persistencePort;
    private final BCryptPasswordEncoder passwordEncoder;

    public CrearPropietarioUseCase(UsuarioPersistencePort persistencePort,
                                   BCryptPasswordEncoder passwordEncoder) {
        this.persistencePort = persistencePort;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario ejecutar(Usuario usuario) {

        // Validar si el documento ya existe
        if (persistencePort.existeDocumento(usuario.getDocumentoIdentidad())) {
            throw new IllegalArgumentException("El documento ya está registrado");
        }

        // 🔹 Encriptar la contraseña antes de guardar
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        // Guardar el usuario
        return persistencePort.guardarUsuario(usuario);
    }
}
