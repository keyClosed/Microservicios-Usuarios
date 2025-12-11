package com.plaza.usuarios_service.infrastructure.configuration;



import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import com.plaza.usuarios_service.domain.usecase.CrearPropietarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BeanConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CrearPropietarioUseCase crearPropietarioUseCase(
            UsuarioPersistencePort usuarioPersistencePort,
            BCryptPasswordEncoder passwordEncoder
    ) {
        return new CrearPropietarioUseCase(usuarioPersistencePort, passwordEncoder);
    }
}