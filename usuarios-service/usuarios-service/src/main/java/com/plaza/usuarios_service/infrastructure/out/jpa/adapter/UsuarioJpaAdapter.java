package com.plaza.usuarios_service.infrastructure.out.jpa.adapter;


import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.spi.UsuarioPersistencePort;
import com.plaza.usuarios_service.infrastructure.out.jpa.entity.UsuarioEntity;
import com.plaza.usuarios_service.infrastructure.out.jpa.mapper.UsuarioEntityMapper;
import com.plaza.usuarios_service.infrastructure.out.jpa.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioJpaAdapter implements UsuarioPersistencePort {

    private final UsuarioRepository repository;
    private final UsuarioEntityMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioJpaAdapter(UsuarioRepository repository,
                             UsuarioEntityMapper mapper,
                             BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario guardarUsuario(Usuario usuario) {

        UsuarioEntity entity = mapper.toEntity(usuario);
        UsuarioEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existeDocumento(String documentoIdentidad) {
        return repository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    public boolean existeCorreo(String correo) {
        return repository.existsByCorreo(correo);
    }

    @Override
    public Usuario obtenerPorCorreo(String correo) {
        return repository.findByCorreo(correo)
                .map(mapper::toDomain)
                .orElse(null);
    }
    @Override
    public Usuario obtenerPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }


}