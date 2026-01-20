package com.plaza.usuarios_service.infrastructure.out.jpa.repository;

import com.plaza.usuarios_service.infrastructure.out.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    boolean existsByDocumentoIdentidad(String documentoIdentidad);

    boolean existsByCorreo(String correo);

    Optional<UsuarioEntity> findByCorreo(String correo); //para login se usa despues

    @Query(value = "SELECT u.restaurante_id FROM usuario u WHERE u.id = :idEmpleado", nativeQuery = true)
    Long findRestauranteIdByEmpleadoId(@Param("idEmpleado") Long idEmpleado);

    long countByRol(String rol);
    boolean existsByCelular(String celular);
}