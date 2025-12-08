package com.plaza.usuarios_service.infrastructure.configuration;

import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import com.plaza.usuarios_service.domain.usecase.CrearPropietarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.plaza.usuarios_service.domain.usecase.LoginUseCase;

@Configuration
public class BeanConfig {

    // Bean del Use Case
    @Bean
    public CrearPropietarioUseCase crearPropietarioUseCase(
            UsuarioPersistencePort port,
            BCryptPasswordEncoder passwordEncoder
    ) {
        return new CrearPropietarioUseCase(port, passwordEncoder);
    }

    // Bean del encoder de contraseñas
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public LoginUseCase loginUseCase(UsuarioPersistencePort persistencePort,
                                     BCryptPasswordEncoder passwordEncoder) {
        return new LoginUseCase(persistencePort, passwordEncoder);
    }

}