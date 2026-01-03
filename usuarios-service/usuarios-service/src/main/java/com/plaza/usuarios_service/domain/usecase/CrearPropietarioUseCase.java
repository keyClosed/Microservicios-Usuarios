package com.plaza.usuarios_service.domain.usecase;

import com.plaza.usuarios_service.domain.api.ICrearPropietarioService;
import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.PasswordEncoderPort;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;

public class CrearPropietarioUseCase implements ICrearPropietarioService {

    private final UsuarioPersistencePort persistencePort;
    private final PasswordEncoderPort passwordEncoder;

    public CrearPropietarioUseCase(UsuarioPersistencePort persistencePort,
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

        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        usuario.setRol("PROPIETARIO");

        return persistencePort.guardarUsuario(usuario);
    }


    @Override
    public Usuario obtenerPorId(Long id) {
        return persistencePort.obtenerPorId(id);
    }
}
