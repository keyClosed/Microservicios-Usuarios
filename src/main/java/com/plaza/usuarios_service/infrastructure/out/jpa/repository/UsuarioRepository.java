package com.plaza.usuarios_service.infrastructure.out.jpa.repository;

import com.plaza.usuarios_service.infrastructure.out.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    boolean existsByDocumentoIdentidad(String documentoIdentidad);

    boolean existsByCorreo(String correo); // 🔹 para validar email existente

    Optional<UsuarioEntity> findByCorreo(String correo); // 🔹 para login
}