package com.plaza.usuarios_service.domain.usecase;

import com.plaza.usuarios_service.domain.api.ICrearEmpleadoService;
import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.PasswordEncoderPort;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;

public class CrearEmpleadoUseCase implements ICrearEmpleadoService {

    private final UsuarioPersistencePort persistencePort;
    private final PasswordEncoderPort passwordEncoder;

    public CrearEmpleadoUseCase(UsuarioPersistencePort persistencePort,
                                PasswordEncoderPort passwordEncoder) {
        this.persistencePort = persistencePort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario ejecutar(Usuario usuario) {

        if (persistencePort.existeDocumento(usuario.getDocumentoIdentidad())) {
            throw new IllegalArgumentException("El documento ya está registrado");
        }

        if (persistencePort.existeCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        usuario.setRol("EMPLEADO");


        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        return persistencePort.guardarUsuario(usuario);
    }
}