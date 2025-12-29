package com.plaza.usuarios_service.infrastructure.configuration;



import com.plaza.usuarios_service.domain.spi.JwtTokenPort;
import com.plaza.usuarios_service.domain.spi.PasswordEncoderPort;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import com.plaza.usuarios_service.domain.usecase.CrearEmpleadoUseCase;
import com.plaza.usuarios_service.domain.usecase.CrearPropietarioUseCase;
import com.plaza.usuarios_service.domain.usecase.LoginUseCase;
import com.plaza.usuarios_service.infrastructure.security.BCryptPasswordEncoderAdapter;
import com.plaza.usuarios_service.infrastructure.security.JwtTokenAdapter;
import com.plaza.usuarios_service.infrastructure.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BeanConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoderPort passwordEncoderPort(BCryptPasswordEncoder encoder) {
        return new BCryptPasswordEncoderAdapter(encoder);
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    public JwtTokenPort jwtTokenPort(JwtUtil jwtUtil) {
        return new JwtTokenAdapter(jwtUtil);
    }


    @Bean
    public CrearPropietarioUseCase crearPropietarioUseCase(
            UsuarioPersistencePort usuarioPersistencePort,
            PasswordEncoderPort passwordEncoderPort
    ) {
        return new CrearPropietarioUseCase(usuarioPersistencePort, passwordEncoderPort);
    }


    @Bean
    public LoginUseCase loginUseCase(
            UsuarioPersistencePort usuarioPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            JwtTokenPort jwtTokenPort
    ) {
        return new LoginUseCase(usuarioPersistencePort, passwordEncoderPort, jwtTokenPort);
    }

    @Bean
    public CrearEmpleadoUseCase crearEmpleadoUseCase(
            UsuarioPersistencePort usuarioPersistencePort,
            PasswordEncoderPort passwordEncoderPort
    ) {
        return new CrearEmpleadoUseCase(usuarioPersistencePort, passwordEncoderPort);
    }
}