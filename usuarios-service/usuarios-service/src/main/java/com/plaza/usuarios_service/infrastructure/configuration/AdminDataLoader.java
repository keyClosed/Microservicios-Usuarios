package com.plaza.usuarios_service.infrastructure.configuration;

import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer {

    private final UsuarioPersistencePort usuarioPersistencePort;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminInitializer(UsuarioPersistencePort usuarioPersistencePort,
                            BCryptPasswordEncoder passwordEncoder) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        String correo = "admin@correo.com";
        if (!usuarioPersistencePort.existeCorreo(correo)) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("Admin");
            admin.setCorreo(correo);
            admin.setClave(passwordEncoder.encode("admin123")); // <<--- clave hasheada
            admin.setRol("ADMIN");

            usuarioPersistencePort.guardarUsuario(admin);
            System.out.println("ADMIN creado correctamente con contraseña: admin123");
        }
    }
}